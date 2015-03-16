from google.appengine.ext import ndb

class Utilisateur(ndb.Model):
    password = ndb.StringProperty()
    nom = ndb.StringProperty()
    prenom = ndb.StringProperty()
    telephone = ndb.StringProperty()
    
class Parcours(ndb.Model):
    typeParcours = ndb.BooleanProperty()
    departLatitude = ndb.StringProperty()
    departLongitude = ndb.StringProperty()
    destinationLatitude = ndb.StringProperty()
    destinationLongitude = ndb.StringProperty()
    departTimestamp = ndb.StringProperty()
    joursRepetes = ndb.IntegerProperty(repeated=True)
    nbPlaces = ndb.IntegerProperty()
    distanceSupplementaire = ndb.FloatProperty()
    notes = ndb.StringProperty()
    actif = ndb.BooleanProperty()
    