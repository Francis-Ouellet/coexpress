from google.appengine.ext import ndb

class Utilisateur(ndb.Model):
    password = ndb.StringProperty()
    nom = ndb.StringProperty()
    prenom = ndb.StringProperty()
    telephone = ndb.StringProperty()
    
class Parcours(ndb.Model):
    proprietaire = ndb.StringProperty()
    typeParcours = ndb.BooleanProperty()
    adresseDepart = ndb.StringProperty()
    departLatitude = ndb.FloatProperty()
    departLongitude = ndb.FloatProperty()
    adresseDestination = ndb.StringProperty()
    destinationLatitude = ndb.FloatProperty()
    destinationLongitude = ndb.FloatProperty()
    departTimestamp = ndb.StringProperty()
    joursRepetes = ndb.IntegerProperty(repeated=True)
    nbPlaces = ndb.IntegerProperty()
    distanceSupplementaire = ndb.FloatProperty()
    notes = ndb.StringProperty()
    actif = ndb.BooleanProperty()
    
class Covoiturage(ndb.Model):
    conducteur = ndb.StringProperty()
    passagers = ndb.StringProperty(repeated=True)
    