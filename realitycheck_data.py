import firebase_admin
from firebase_admin import credentials, firestore
import json

cred = credentials.Certificate("C:/Users/Matthew Benvenuto/Downloads/realitycheck-d5694-firebase-adminsdk-qu5oe-f20fb26451.json")
firebase_admin.initialize_app(cred)
db = firestore.client()
fh = open('realitycheck.txt', 'w')

""" user_refrences = db.collection(u'Users').document(u'Griffin')
doc = user_refrences.get()
if doc.exists:
    print(f'Document data: {doc.todict()}')
else:
    print(u'No such document!') """

def getUserData():
    collection = db.collection("Users")
    docs = []
    count = 1
    for doc in collection.stream():  
        formattedData = doc.to_dict()
        print(formattedData)
        fh.write(json.dumps(formattedData))
        
        print("-------------------------------------organized portion---------------------------")
        docs.append(doc.reference)
        count= count +1
    print(docs)

def getGroupsData():
    collection = db.collection("Groups")
    docs = []
    count = 1
    for doc in collection.stream():  
        formattedData = doc.to_dict()
        print(formattedData)
        fh.write(json.dumps(formattedData))
        print("-------------------------------------organized portion---------------------------")
        docs.append(doc.reference)
        count= count +1
    print(docs)
def getPostsData():
    collection = db.collection("Posts")
    docs = []
    for doc in collection.stream():  
        formattedData = doc.to_dict()
        print(formattedData)
        fh.write(json.dumps(formattedData))
        print("-------------------------------------organized portion---------------------------")
        docs.append(doc.reference)
    print(docs)
print(".......................................USERS DATA...........................................................")
print("")
getUserData()
print(".......................................GROUPS DATA.........................................................")
print("")
getGroupsData()
print("....................................POSTS DATA..............................................................")
print("")
getPostsData()
print("Succesfully printed all the components from realtime Firestore Database")
fh.close()


