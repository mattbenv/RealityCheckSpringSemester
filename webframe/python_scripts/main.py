import firebase_admin
import collections
from firebase_admin import credentials, firestore
import json
from collections import Counter
import matplotlib.pyplot as plt


cred = credentials.Certificate('/Users/masterp/Desktop/Reality/realitycheck-d5694-firebase-adminsdk-qu5oe-2ab4ce0b60.json')
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
    return docs

def getDate():
    collection = db.collection("Posts")
    dates = []
    for item in collection.stream():
        formattedData = item.to_dict()
        if 'postDate' in formattedData:
            dates.append(formattedData.get('postDate'))
            c = Counter()
    c.update(dates)
    dates = list(c.keys())
    day = []
    month = []
    for date in dates:
        day.append(date.split(",", 1)[0])
    for first in day:
        month.append(first.split(" ", 1)[0])

    c2 = Counter()
    c2.update(month)
    months = list(c2.keys())
    posts = list(c2.values())
    plt.bar(range(len(c2)), posts, tick_label=months, color='purple')
    plt.title('Number of Posts in a month')
    plt.xlabel('Months')
    plt.ylabel('# of Posts')
    plt.show()


    """
    dates = []
    for item in docs:
        if item.has_key('commentDate'):
            dates.append(item.get('commentDate'))
    c = Counter()
    c.update(dates)
    print(c)
    """


##print(".......................................USERS DATA...........................................................")
##print("")
##getUserData()
##print(".......................................GROUPS DATA.........................................................")
##print("")
##getGroupsData()
##print("....................................POSTS DATA..............................................................")
##print("")
getDate()
print("Succesfully printed all the components from realtime Firestore Database")
fh.close()