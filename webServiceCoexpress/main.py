# -*- coding: utf-8 -*-

from google.appengine.ext import ndb
from google.appengine.ext import db
from modeles import Utilisateur, Parcours
import webapp2
import logging
import json

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
                elif(jsonObj['previousPassword'] is not None and utilisateur.password == jsonObj['previousPassword']):
                    if(jsonObj['nom'] is not None):
                        utilisateur.nom = jsonObj['nom']
                    if(jsonObj['prenom'] is not None):
                        utilisateur.prenom = jsonObj['prenom']
                    if(jsonObj['password'] is not None):
                        utilisateur.password = jsonObj['password']
                    if(jsonObj['telephone'] is not None):
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
                    jsonObj = json.loads(self.request.body)
                    if(cle.get().password == jsonObj['password']):
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
            utilisateur = ndb.key('Utilisateur', username).get()
            if(utilisateur is not None):
                # Identifiant non fourni, retourne tous les parcours
                if(idParcours is None):
                    resultat = []
                    query = Parcours.query(Parcours.proprietaire == username)
                    
                    for p in query:
                        resultat.append(p.to_dict())
                # Retourne le parcours identifié
                else:
                    parcours = ndb.key('Parcours', idParcours).get()
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
            if(idParcours is None):
                self.error(500)
                return
            else:
                cle = ndb.key('Parcours', idParcours)
                parcours = cle.get()
                jsonObj = json.loads(self.request.body)
                status = 204
                # Ajout ou modification du parcours
                parcours = Utilisateur(key=cle)
                parcours.proprietaire = jsonObj['proprietaire']
                parcours.typeParcours = jsonObj['typeParcours']
                parcours.departLatitude = jsonObj['departLatitude']
                parcours.departLongitude = jsonObj['departLongitude']
                parcours.destinationLatitude = jsonObj['destinationLatitude']
                parcours.destinationLongitude = jsonObj['destinationLongitude']
                parcours.departTimestamp = jsonObj['departTimestamp']
                parcours.joursRepetes = jsonObj['joursRepetes']
                parcours.nbPlaces = jsonObj['nbPlaces']
                parcours.distanceSupplementaire = jsonObj['distanceSupplementaire']
                parcours.notes = jsonObj['notes']
                parcours.actif = jsonObj['actif']
                parcours.put()
                status = 201
                
            self.response.set_status(status)
                
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
        
        except Exception, ex:
            logging.exception(ex)
            self.error(500)
    def delete(self, username = None, idParcours = None):            
        try:
            utilisateur = ndb.key('Utilisateur', username).get()
            if(utilisateur is None):
                self.error(404)
                return
            else:
                jsonObj = json.loads(self.request.body)
                if(utilisateur.password == jsonObj['password']):
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
            
application = webapp2.WSGIApplication(
    [
        ('/',   MainPageHandler),
        webapp2.Route(r'/utilisateurs',handler=UtilisateurHandler, methods=['GET', 'DELETE']),
        webapp2.Route(r'/utilisateurs/<username>',handler=UtilisateurHandler, methods=['GET','PUT', 'DELETE']),
        webapp2.Route(r'/utilisateurs/<username>/parcours',handler=ParcoursHandler, methods=['GET', 'DELETE']),
        webapp2.Route(r'/utilisateurs/<username>/parcours/<idParcours>',handler=ParcoursHandler, methods=['GET', 'PUT', 'DELETE'])
        
    ],
    debug=True)            