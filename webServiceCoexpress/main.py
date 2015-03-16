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
                    resultat.append(u.to_dict(exclude='password'))
            
            else:
                utilisateur = ndb.Key('Utilisateur', username).get()
                # L'utilisateur n'existe pas
                if(utilisateur is None):
                    self.response.set_status(404)
                    return
                resultat = utilisateur.to_dict(exclude='password')
            
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
            resultat = None
        except (ValueError, db.BadValueError), ex:
            logging.info(ex)
            self.error(400)
        
        except Exception, ex:
            logging.exception(ex)
            self.error(500)
        
    def delete(self, username = None):
        try:
            resultat = None
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
        webapp2.Route(r'/utilisateurs/<username>',handler=UtilisateurHandler, methods=['GET','PUT', 'DELETE'])
        
    ],
    debug=True)            