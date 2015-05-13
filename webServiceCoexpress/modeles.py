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
    
class Commentaire(ndb.Model):
    proprietaire = ndb.StringProperty()
    auteur = ndb.StringProperty()
    timestampCreation = ndb.StringProperty()
    texte = ndb.StringProperty()
    upvotes = ndb.IntegerProperty()
    downvotes = ndb.IntegerProperty()
    
class CommentaireVote(ndb.Model):
    idCommentaire = ndb.StringProperty()
    idVoteur = ndb.StringProperty()
    # True vaut +1
    # False vaut -1
    typeVote = ndb.BooleanProperty()
    