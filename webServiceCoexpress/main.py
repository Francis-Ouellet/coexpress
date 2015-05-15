# -*- coding: utf-8 -*-

from google.appengine.ext import ndb
from google.appengine.ext import db
from modeles import Utilisateur, Parcours, Covoiturage, Commentaire, CommentaireVote
from math import hypot, fabs
from datetime import datetime, date
import webapp2
import logging
import json


def verifier_compatibilite_parcours(conducteur, passager):
    # Si le conducteur est actif
    # Si le passager est actif
    # Si le nombre de places disponibles du conducteur est supérieur au nombre de places demandées du passager
    # TODO : VÉRIFIER LA DATE AUSSI !
    if(conducteur.actif and passager.actif and conducteur.nbPlaces >= passager.nbPlaces):
        
        # Transforme le timestamp en objet Datetime
        departConducteur = datetime.fromtimestamp(float(conducteur.departTimestamp) / 1e3)
        departPassager = datetime.fromtimestamp(float(passager.departTimestamp) / 1e3)
        
        # Indicateur de jour correspondant entre le conducteur et le passager
        found = False
        # Si les dates de départ du conducteur et du passager ont au maximum 24h d'écart
        # et qu'elles sont dans le futur
        if(departConducteur > datetime.today() and departPassager > datetime.today() and 
           fabs((departConducteur - departPassager).total_seconds()) < 24*60*60):
            found = True
        else:   
            i = 0
            # Tant que l'itérateur est plus petit que le nombre de répétitions et qu'un match n'est pas trouvé
            while (i < len(conducteur.joursRepetes) and not found):
                j = 0
                # Si un des jours répétés du conducteur est le jour de départ du passager
                found = comparer_jours_semaine(conducteur.joursRepetes[i], departPassager.weekday())
                
                while(j < len(passager.joursRepetes) and not found):
                    # Si deux jours sont identiques dans les jours répétés
                    # Ou si un des jours répétés du passager est le jour du départ du conducteur
                    if((conducteur.joursRepetes[i] == passager.joursRepetes[j] or
                       comparer_jours_semaine(passager.joursRepetes[j], departConducteur.weekday()))):
                        found = True
                    else:
                        j = j + 1
                i = i + 1
        
        # Si les dates de départ correspondent, on vérifie si la différence 
        # entre les heures dedépart est inférieure à 2h
        if(found):
            # Python ne permet pas de soustraire directement deux objets "time",
            # Il faut donc former un objet "datetime" complet en ajoutant la date du jour aux deux valeurs de temps
            delta = (datetime.combine(date.today(), departConducteur.time()) - 
                        datetime.combine(date.today(), departPassager.time())).total_seconds()
            if(delta < 0):
                delta = delta + 24*60*60
            # Si la différence entre le départ du conducteur et du passager est inférieure à 2h
            if(delta < 2*60*60):
                return True
            else:
                return False
        else:
            return False
    else:
        return False

# Permet de comparer la notation numérique des jours de la semaine selon la conversion suivante :
#    Dimanche    Lundi    Mardi    Mercredi    Jeudi    Vendredi    Samedi
#a:  1           2        3        4           5        6           7
#b:  6           0        1        2           3        4           5
def comparer_jours_semaine(a,b):
    if(a-2 == b):
        return True
    elif(a+5 == b):
        return True
    else:
        return False
    
def serialiser_parcours_covoiturage(idParcours):
    parcours = ndb.Key('Parcours', idParcours).get()
    parcoursJSON = parcours.to_dict()
    parcoursJSON['idParcours'] = parcours.key.id()
    parcoursJSON['nom'] = ndb.Key('Utilisateur', parcours.proprietaire).get().nom
    parcoursJSON['prenom'] = ndb.Key('Utilisateur', parcours.proprietaire).get().prenom
    return parcoursJSON

# Permet de vérifier si un covoiturage existe déjà pour ce conducteur et ce passager au jour spécifié
# TODO Revérifier cet algorithme
def verifier_covoiturage_existant(idConducteur, idPassager, jour):
    covoiturage = ndb.Key('Covoiturage', idConducteur + str(jour)).get()
    logging.info(covoiturage)
    logging.info(idConducteur + str(jour))
    logging.info(idPassager)
    if(covoiturage is not None):
        found = False
        for passager in covoiturage.passagers:
            if(passager == idPassager):
                found = True
        return found
    else:
        return False

# Compte le nombre de votes positifs et négatifs d'un commentaire pour obtenir le score de ce commentaire    
def obtenir_points_commentaire(idCommentaire):
    query = CommentaireVote.query(CommentaireVote.idCommentaire == idCommentaire)
    upvotes = 0
    downvotes = 0
    for v in query:
        if(v.typeVote):
            upvotes = upvotes + 1
        else:
            downvotes = downvotes + 1
            
    return [upvotes, downvotes]
    
class MainPageHandler(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/html; charset=utf-8'
        self.response.out.write('<html><body><h1>Demo Google App Engine fonctionne bien !</h1></body></html>')
        
class UtilisateurHandler(webapp2.RequestHandler):
    def get(self, username = None):
        try:
            resultat = None
            
            # Liste complète des utilisateurs
            if(username is None):
                resultat = []
                query = Utilisateur.query().order(Utilisateur.key)
                
                for u in query:
                    dictUtilisateur = u.to_dict(exclude=['password'])
                    dictUtilisateur['courriel'] = u.key.id()
                    resultat.append(dictUtilisateur)
            else:
                utilisateur = ndb.Key('Utilisateur', username).get()
                # L'utilisateur n'existe pas
                if(utilisateur is None):
                    self.error(404)
                    return
                resultat = utilisateur.to_dict()
                resultat['courriel'] = utilisateur.key.id()
            
            self.response.headers['Content-Type'] = 'application/json'
            self.response.out.write(json.dumps(resultat))
            
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
        
        except Exception, ex:
            logging.exception(ex)
            self.error(500)
            
    def put(self, username):
        try:
            if(username is None):
                self.error(500)
                return
            else:
                cle = ndb.Key('Utilisateur', username)
                utilisateur = cle.get()
                jsonObj = json.loads(self.request.body)
                status = 204
                # Ajout de l'utilisateur
                if(utilisateur is None):
                    utilisateur = Utilisateur(key=cle)
                    utilisateur.nom = jsonObj['nom']
                    utilisateur.prenom = jsonObj['prenom']
                    utilisateur.password = jsonObj['password']
                    utilisateur.telephone = jsonObj['telephone']
                    utilisateur.put()
                    status = 201
                elif(jsonObj.get('previousPassword') is not None and utilisateur.password == jsonObj['previousPassword']):
                    if(jsonObj.get('nom') is not None):
                        utilisateur.nom = jsonObj['nom']
                    if(jsonObj.get('prenom') is not None):
                        utilisateur.prenom = jsonObj['prenom']
                    if(jsonObj.get('password') is not None):
                        utilisateur.password = jsonObj['password']
                    if(jsonObj.get('telephone') is not None):
                        utilisateur.telephone = jsonObj['telephone']
                    utilisateur.put()
                    status = 201
            self.response.set_status(status)
            
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
        
        except Exception, ex:
            logging.exception(ex)
            self.error(500)
        
    def delete(self, username = None):
        try:
            if(username is None):
                ndb.delete_multi(Utilisateur.query().fetch(keys_only=True))
                status = 204
            else:
                cle = ndb.Key('Utilisateur', username)
                if(cle.get() is not None):
                    if(cle.get().password == self.request.get('password')):
                        cle.delete()
                        status = 204
                else:
                    status = 404
            
            self.response.set_status(status)
                    
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
        
        except Exception, ex:
            logging.exception(ex)
            self.error(500)
            
class ParcoursHandler(webapp2.RequestHandler):
    def get(self, username = None, idParcours = None):
        try:
            resultat = None
            utilisateur = ndb.Key('Utilisateur', username).get()
            if(utilisateur is not None):
                # Identifiant non fourni, retourne tous les parcours
                if(idParcours is None):
                    resultat = []
                    query = Parcours.query(Parcours.proprietaire == username)
                    
                    for p in query:
                        dictParcours = p.to_dict()
                        dictParcours['idParcours'] = p.key.id()
                        resultat.append(dictParcours)
                # Retourne le parcours identifié
                else:
                    parcours = ndb.Key('Parcours', idParcours).get()
                    if(parcours is not None):
                        resultat = parcours.to_dict()
                    
            else:
                self.error(404)    
            
            self.response.headers['Content-Type'] = 'application/json'
            self.response.out.write(json.dumps(resultat))
                
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
        
        except Exception, ex:
            logging.exception(ex)
            self.error(500)
    def put(self, username, idParcours):
        try:
            utilisateur = ndb.Key('Utilisateur', username).get()
            if(utilisateur is None):
                self.error(400)
                return
            else:
                if(idParcours is None):
                    self.error(400)
                    return
                else:
                    cle = ndb.Key('Parcours', idParcours)
                    parcours = cle.get()
                    jsonObj = json.loads(self.request.body)
                    status = 204
                    if(utilisateur.password == jsonObj.get('password')):
                        # Ajout ou modification du parcours
                        parcours = Parcours(key=cle)
                        parcours.proprietaire = jsonObj['proprietaire']
                        parcours.typeParcours = jsonObj['typeParcours']
                        parcours.adresseDepart = jsonObj['adresseDepart']
                        parcours.departLatitude = jsonObj['departLatitude']
                        parcours.departLongitude = jsonObj['departLongitude']
                        parcours.adresseDestination = jsonObj['adresseDestination']
                        parcours.destinationLatitude = jsonObj['destinationLatitude']
                        parcours.destinationLongitude = jsonObj['destinationLongitude']
                        parcours.departTimestamp = jsonObj['departTimestamp']
                        if(jsonObj.get('joursRepetes') is not None):
                            parcours.joursRepetes = map(int,jsonObj['joursRepetes'].replace("[","").replace("]","").split(", "))
                        parcours.nbPlaces = jsonObj['nbPlaces']
                        parcours.distanceSupplementaire = jsonObj['distanceSupplementaire']
                        parcours.notes = jsonObj['notes']
                        parcours.actif = jsonObj['actif']
                        parcours.put()
                        status = 201
                    else:
                        self.error(401)
                        return
            self.response.set_status(status)
                
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
        
        except Exception, ex:
            logging.exception(ex)
            self.error(500)
    def delete(self, username = None, idParcours = None):            
        try:
            utilisateur = ndb.Key('Utilisateur', username).get()
            if(utilisateur is None):
                self.error(404)
                return
            else:
                if(utilisateur.password == self.request.get('password')):
                    if(idParcours is None):
                        ndb.delete_multi(Parcours.query().fetch(keys_only=True))
                        status = 204
                    else:
                        cle = ndb.Key('Parcours', idParcours)
                        if(cle.get() is not None):
                            cle.delete()
                            status = 204
                        else:
                            self.error(404)
                else:
                    self.error(403)
            
            self.response.set_status(status)
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
        
        except Exception, ex:
            logging.exception(ex)
            self.error(500)
            
class ChercherParcoursHandler(webapp2.RequestHandler):
    def get(self, username, idParcours):
        try:
            resultat = []
            utilisateur = ndb.Key('Utilisateur', username).get()
            if(utilisateur is not None):
                parcoursDemandeur = ndb.Key('Parcours', idParcours).get()
                if(parcoursDemandeur is not None):
                    # Obtient la liste des parcours de type opposé au parcours demandeur
                    query = Parcours.query(Parcours.typeParcours != parcoursDemandeur.typeParcours)
                    
                    # Le demandeur est un conducteur
                    if(parcoursDemandeur.typeParcours):
                        
                        distanceConducteur = hypot(
                            parcoursDemandeur.destinationLatitude - parcoursDemandeur.departLatitude,
                            parcoursDemandeur.destinationLongitude - parcoursDemandeur.departLongitude)
                        
                        # Pour tous les passagers potentiels
                        for parcoursPassager in query:
                            
                            if(verifier_compatibilite_parcours(parcoursDemandeur, parcoursPassager)):
                            
                                distanceOrigines = hypot(
                                    parcoursPassager.departLatitude - parcoursDemandeur.departLatitude,
                                    parcoursPassager.departLongitude - parcoursDemandeur.departLongitude)
                                distanceDestinations = hypot(
                                    parcoursPassager.destinationLatitude - parcoursDemandeur.destinationLatitude,
                                    parcoursPassager.destinationLongitude - parcoursDemandeur.destinationLongitude)
                                
                                if(distanceConducteur + distanceOrigines + distanceDestinations 
                                    < distanceConducteur * parcoursDemandeur.distanceSupplementaire):
                                    dictParcours = parcoursPassager.to_dict()
                                    dictParcours['idParcours'] = parcoursPassager.key.id()
                                    resultat.append(dictParcours)
                                    
                    # Le demandeur est un passager
                    else:
                        # Pour tous les conducteurs potentiels
                        for parcoursConducteur in query:
                            
                            if(verifier_compatibilite_parcours(parcoursConducteur, parcoursDemandeur)):
                                
                                distanceConducteur = hypot(
                                    parcoursConducteur.destinationLatitude - parcoursConducteur.departLatitude,
                                    parcoursConducteur.destinationLongitude - parcoursConducteur.departLongitude)
                                
                                distanceOrigines = hypot(
                                    parcoursDemandeur.departLatitude - parcoursConducteur.departLatitude,
                                    parcoursDemandeur.departLongitude - parcoursConducteur.departLongitude)
                                
                                distanceDestinations = hypot(
                                    parcoursDemandeur.destinationLatitude - parcoursConducteur.destinationLatitude,
                                    parcoursDemandeur.destinationLongitude - parcoursConducteur.destinationLongitude)
                                
                                if(distanceConducteur + distanceOrigines + distanceDestinations
                                   < distanceConducteur * parcoursConducteur.distanceSupplementaire):
                                    dictParcours = parcoursConducteur.to_dict()
                                    dictParcours['idParcours'] = parcoursConducteur.key.id()
                                    resultat.append(dictParcours)
                    
                else:
                    self.error(404)
                    return
            else:
                self.error(404)
                return
            
            self.response.headers['Content-Type'] = 'application/json'
            self.response.out.write(json.dumps(resultat))
            
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
        
        except Exception, ex:
            logging.exception(ex)
            self.error(500)  
        
class CovoiturageHandler(webapp2.RequestHandler):
    def get(self, username, idParcours):
        try:
            resultat = []
            utilisateur = ndb.Key('Utilisateur', username).get()
            if(utilisateur is not None):
                parcoursDemandeur = ndb.Key('Parcours', idParcours).get()
                if(parcoursDemandeur is not None):
                    if (len(parcoursDemandeur.joursRepetes) > 0):
                        # Pour chacun des jours répétés du demandeur
                        for jour in parcoursDemandeur.joursRepetes:
                            covoiturage = ndb.Key('Covoiturage', idParcours + str(jour)).get()
                            if(covoiturage is not None):
                                sousResultat = []
                                sousResultat.append(serialiser_parcours_covoiturage(covoiturage.conducteur))
                                for passager in covoiturage.passagers:
                                    sousResultat.append(serialiser_parcours_covoiturage(passager))
                                resultat.append(sousResultat)
                    
                    covoiturage = ndb.Key('Covoiturage', idParcours).get()
                    if(covoiturage is not None):
                        sousResultat = []
                        sousResultat.append(serialiser_parcours_covoiturage(covoiturage.conducteur))
                        for passager in covoiturage.passagers:
                            sousResultat.append(serialiser_parcours_covoiturage(passager))
                        resultat.append(sousResultat)
                else:
                    self.error(404)
                    return
            else:
                self.error(404)
                return
            
            self.response.headers['Content-Type'] = 'application/json'
            self.response.out.write(json.dumps(resultat))
            
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
            
        except Exception, ex:
            logging.exception(ex)
            self.error(500)  
            
    def put(self, username, idParcours, idParcoursAjoute):
        try:
            utilisateur = ndb.Key('Utilisateur', username).get()
            if(utilisateur is not None):
                parcoursDemandeur = ndb.Key('Parcours', idParcours).get()
                if(parcoursDemandeur is not None):
                    parcoursDesire = ndb.Key('Parcours', idParcoursAjoute).get()
                    if(parcoursDesire is not None):
                        jsonObj = json.loads(self.request.body)
                        cle = ndb.Key('Covoiturage', idParcours + jsonObj['jour'])
                        if(utilisateur.password == jsonObj.get('password')):
                            covoiturage = Covoiturage(key=cle)
                            
                            # Demandeur conducteur
                            if(parcoursDemandeur.typeParcours):
                                covoiturage.conducteur = idParcours
                                covoiturage.passagers.append(idParcoursAjoute)
                                # Réduction du nombre de places disponibles du conducteur
                                parcours = Parcours(key=parcoursDemandeur.key)
                                parcours.nbPlaces = parcoursDemandeur.nbPlaces - parcoursDesire.nbPlaces
                                parcours.put()
                            # Demandeur passager et désiré conducteur
                            elif(parcoursDesire.typeParcours):
                                covoiturage.conducteur = idParcoursAjoute
                                covoiturage.passagers.append(idParcours)
                                # Réduction du nombre de places disponibles du conducteur
                                parcours = Parcours(key=parcoursDesire.key)
                                parcours.nbPlaces = parcoursDesire.nbPlaces - parcoursDemandeur.nbPlaces
                                parcours.put()
                                
                            covoiturage.put()
                        else:
                            self.error(401)
                            return
                    else:
                        self.error(404)
                        return
                else:
                    self.error(404)
                    return
            else:
                self.error(404)
                return
            
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
            
        except Exception, ex:
            logging.exception(ex)
            self.error(500)
            
class CommentairesHandler(webapp2.RequestHandler):
    def get(self, username, idCommentaire = None):
        try:
            resultat = None
            utilisateur = ndb.Key('Utilisateur', username).get()
            if(utilisateur is not None):
                if(idCommentaire is None):
                    # Tous les commentaires
                    resultat = []
                    query = Commentaire.query(Commentaire.proprietaire == username)
                    
                    for c in query:
                        votes = obtenir_points_commentaire(c.key.id())
                        dictParcours = c.to_dict()
                        dictParcours['idCommentaire'] = c.key.id()
                        dictParcours['upvotes'] = votes[0]
                        dictParcours['downvotes'] = votes[1] 
                        resultat.append(dictParcours)
                else:
                    # Ce commentaire en particulier
                    commentaire = ndb.Key('Commentaire', idCommentaire).get()
                    if(commentaire is not None):
                        votes = obtenir_points_commentaire(c.key.id())
                        resultat = commentaire.to_dict()
                        resultat['idCommentaire'] = c.key.id()
                        dictParcours['upvotes'] = votes[0]
                        dictParcours['downvotes'] = votes[1]
                    else:
                        self.error(404)
            else:
                self.error(404)
                return
            
            self.response.headers['Content-Type'] = 'application/json'
            self.response.out.write(json.dumps(resultat))
            
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
            
        except Exception, ex:
            logging.exception(ex)
            self.error(500)
    
    def put(self, username, idCommentaire):
        try:
            utilisateur = ndb.Key('Utilisateur',username).get()
            if(utilisateur is not None and idCommentaire is not None):
                cle = ndb.Key('Commentaire', idCommentaire)
                jsonObj = json.loads(self.request.body)
                # Ajout ou modification du commentaire
                commentaire = Commentaire(key=cle)
                commentaire.proprietaire = username
                commentaire.auteur = jsonObj['auteur']
                commentaire.timestampCreation = jsonObj['timestampCreation']
                commentaire.texte = jsonObj['texte']
                commentaire.upvotes = jsonObj['upvotes']
                commentaire.downvotes = jsonObj['downvotes']
                commentaire.put()
                status = 201
                self.response.set_status(status)
            else:
                self.error(404)
                return
                
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
            
        except Exception, ex:
            logging.exception(ex)
            self.error(500)
            
    def delete(self, username, idCommentaire):
        try:
            utilisateur = ndb.Key('Utilisateur', username).get()
            if(utilisateur is not None):
                cle = ndb.Key('Commentaire', idCommentaire)
                if(cle is not None):
                    if(utilisateur.password == self.request.get('password')):
                        if(cle.get() is not None):
                            cle.delete()
                            ndb.delete_multi(CommentaireVote.query(CommentaireVote.idCommentaire == idCommentaire)
                                             .fetch(keys_only=True))
                            self.response.set_status(204)
                    else:
                        self.error(403)
                        return
                else:
                    self.error(404)
                    return
            else:
                self.error(404)
                return
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
            
        except Exception, ex:
            logging.exception(ex)
            self.error(500)    
            
class VoteHandler(webapp2.RequestHandler):
    def put(self, username, idCommentaire, usernameVoteur):
        try:
            utilisateur = ndb.Key('Utilisateur',username).get()
            if(utilisateur is not None):
                commentaire = ndb.Key('Commentaire',idCommentaire).get()
                if(commentaire is not None):
                    voteur = ndb.Key('Utilisateur',usernameVoteur).get()
                    if(voteur is not None):
                        
                        cle = ndb.Key('CommentaireVote', idCommentaire + usernameVoteur)
                        jsonObj = json.loads(self.request.body)
                        status = 204
                        
                        if(voteur.password == jsonObj['password']):
                            commentaireVote = CommentaireVote(key = cle)
                            commentaireVote.idCommentaire = idCommentaire
                            commentaireVote.idVoteur = usernameVoteur
                            commentaireVote.typeVote = jsonObj['typeVote']
                            commentaireVote.put()
                            status = 201
                        else:
                            self.error(401)
                            return
                        
                        self.response.set_status(status)
                    else:
                        self.error(404)
                        return
                else:
                    self.error(404)
                    return
            else:
                self.error(404)
                return
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
            
        except Exception, ex:
            logging.exception(ex)
            self.error(500)
            
application = webapp2.WSGIApplication(
    [
        ('/',   MainPageHandler),
        
        webapp2.Route(r'/utilisateurs',
                      handler=UtilisateurHandler, methods=['GET', 'DELETE']),
        webapp2.Route(r'/utilisateurs/<username>',
                      handler=UtilisateurHandler, methods=['GET','PUT', 'DELETE']),
        webapp2.Route(r'/utilisateurs/<username>/parcours',
                      handler=ParcoursHandler, methods=['GET', 'DELETE']),
        webapp2.Route(r'/utilisateurs/<username>/parcours/<idParcours>',
                      handler=ParcoursHandler, methods=['GET', 'PUT', 'DELETE']),
        webapp2.Route(r'/utilisateurs/<username>/parcours/<idParcours>/chercher',
                      handler=ChercherParcoursHandler, methods=['GET']),
        webapp2.Route(r'/utilisateurs/<username>/parcours/<idParcours>/ajouter/<idParcoursAjoute>',
                      handler=CovoiturageHandler, methods=['PUT']),
        webapp2.Route(r'/utilisateurs/<username>/parcours/<idParcours>/participants',
                      handler=CovoiturageHandler, methods=['GET']),
        webapp2.Route(r'/utilisateurs/<username>/commentaires',
                      handler=CommentairesHandler, methods=['GET']),
        webapp2.Route(r'/utilisateurs/<username>/commentaires/<idCommentaire>',
                      handler=CommentairesHandler, methods=['GET', 'PUT']),
        webapp2.Route(r'/utilisateurs/<username>/commentaires/<idCommentaire>/vote/<usernameVoteur>',
                      handler=VoteHandler, methods=['PUT'])
        
    ],
    debug=True)            