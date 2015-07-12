import socket
import json
import collections
import uuid

class GetShopBaseClass(object):
  pass
class EmptyClass(object):
  pass

class CommunicationHelper:
  def __init__(self, address):
    self.host = address
    self.port = 25554 
    self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.socket.connect((self.host, self.port))
    self.sessionId = str(uuid.uuid4())

  def sendMessage(self, message):
    message.sessionId = self.sessionId
    jsonMessage = json.dumps(message.__dict__)
    self.socket.send(jsonMessage+"\n")
    data = ""
    chars = []
    while True:
        a = self.socket.recv(1)
        chars.append(a)
        if a == "\n" or a == "":
           data = "".join(chars)
           break
    data = json.loads(data)
    if "errorCode" in data:
      raise Exception("Server responded with errorcode "+str(data["errorCode"])+" ")
    return data

  def close(self):
    self.socket.close();

class ArxManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addCard(self, personId, card):
    args = collections.OrderedDict()
    if isinstance(personId,GetShopBaseClass): 
      args["personId"]=json.dumps(personId.__dict__)
    else:
      try:
        args["personId"]=json.dumps(personId)
      except (ValueError, AttributeError):
        args["personId"]=personId
    if isinstance(card,GetShopBaseClass): 
      args["card"]=json.dumps(card.__dict__)
    else:
      try:
        args["card"]=json.dumps(card)
      except (ValueError, AttributeError):
        args["card"]=card
    data = EmptyClass()
    data.args = args
    data.method = "addCard"
    data.interfaceName = "core.arx.IArxManager"
    return self.communicationHelper.sendMessage(data)

  def doorAction(self, externalId, state):
    args = collections.OrderedDict()
    if isinstance(externalId,GetShopBaseClass): 
      args["externalId"]=json.dumps(externalId.__dict__)
    else:
      try:
        args["externalId"]=json.dumps(externalId)
      except (ValueError, AttributeError):
        args["externalId"]=externalId
    if isinstance(state,GetShopBaseClass): 
      args["state"]=json.dumps(state.__dict__)
    else:
      try:
        args["state"]=json.dumps(state)
      except (ValueError, AttributeError):
        args["state"]=state
    data = EmptyClass()
    data.args = args
    data.method = "doorAction"
    data.interfaceName = "core.arx.IArxManager"
    return self.communicationHelper.sendMessage(data)

  def getAllAccessCategories(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllAccessCategories"
    data.interfaceName = "core.arx.IArxManager"
    return self.communicationHelper.sendMessage(data)

  def getAllDoors(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllDoors"
    data.interfaceName = "core.arx.IArxManager"
    return self.communicationHelper.sendMessage(data)

  def getAllPersons(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllPersons"
    data.interfaceName = "core.arx.IArxManager"
    return self.communicationHelper.sendMessage(data)

  def getLogForDoor(self, externalId, start, end):
    args = collections.OrderedDict()
    if isinstance(externalId,GetShopBaseClass): 
      args["externalId"]=json.dumps(externalId.__dict__)
    else:
      try:
        args["externalId"]=json.dumps(externalId)
      except (ValueError, AttributeError):
        args["externalId"]=externalId
    if isinstance(start,GetShopBaseClass): 
      args["start"]=json.dumps(start.__dict__)
    else:
      try:
        args["start"]=json.dumps(start)
      except (ValueError, AttributeError):
        args["start"]=start
    if isinstance(end,GetShopBaseClass): 
      args["end"]=json.dumps(end.__dict__)
    else:
      try:
        args["end"]=json.dumps(end)
      except (ValueError, AttributeError):
        args["end"]=end
    data = EmptyClass()
    data.args = args
    data.method = "getLogForDoor"
    data.interfaceName = "core.arx.IArxManager"
    return self.communicationHelper.sendMessage(data)

  def getPerson(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getPerson"
    data.interfaceName = "core.arx.IArxManager"
    return self.communicationHelper.sendMessage(data)

  def isLoggedOn(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "isLoggedOn"
    data.interfaceName = "core.arx.IArxManager"
    return self.communicationHelper.sendMessage(data)

  def logonToArx(self, hostname, username, password):
    args = collections.OrderedDict()
    if isinstance(hostname,GetShopBaseClass): 
      args["hostname"]=json.dumps(hostname.__dict__)
    else:
      try:
        args["hostname"]=json.dumps(hostname)
      except (ValueError, AttributeError):
        args["hostname"]=hostname
    if isinstance(username,GetShopBaseClass): 
      args["username"]=json.dumps(username.__dict__)
    else:
      try:
        args["username"]=json.dumps(username)
      except (ValueError, AttributeError):
        args["username"]=username
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "logonToArx"
    data.interfaceName = "core.arx.IArxManager"
    return self.communicationHelper.sendMessage(data)

  def updatePerson(self, person):
    args = collections.OrderedDict()
    if isinstance(person,GetShopBaseClass): 
      args["person"]=json.dumps(person.__dict__)
    else:
      try:
        args["person"]=json.dumps(person)
      except (ValueError, AttributeError):
        args["person"]=person
    data = EmptyClass()
    data.args = args
    data.method = "updatePerson"
    data.interfaceName = "core.arx.IArxManager"
    return self.communicationHelper.sendMessage(data)

class BannerManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addImage(self, id, fileId):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    if isinstance(fileId,GetShopBaseClass): 
      args["fileId"]=json.dumps(fileId.__dict__)
    else:
      try:
        args["fileId"]=json.dumps(fileId)
      except (ValueError, AttributeError):
        args["fileId"]=fileId
    data = EmptyClass()
    data.args = args
    data.method = "addImage"
    data.interfaceName = "app.banner.IBannerManager"
    return self.communicationHelper.sendMessage(data)

  def createSet(self, width, height, id):
    args = collections.OrderedDict()
    if isinstance(width,GetShopBaseClass): 
      args["width"]=json.dumps(width.__dict__)
    else:
      try:
        args["width"]=json.dumps(width)
      except (ValueError, AttributeError):
        args["width"]=width
    if isinstance(height,GetShopBaseClass): 
      args["height"]=json.dumps(height.__dict__)
    else:
      try:
        args["height"]=json.dumps(height)
      except (ValueError, AttributeError):
        args["height"]=height
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "createSet"
    data.interfaceName = "app.banner.IBannerManager"
    return self.communicationHelper.sendMessage(data)

  def deleteSet(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "deleteSet"
    data.interfaceName = "app.banner.IBannerManager"
    return self.communicationHelper.sendMessage(data)

  def getSet(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getSet"
    data.interfaceName = "app.banner.IBannerManager"
    return self.communicationHelper.sendMessage(data)

  def linkProductToImage(self, bannerSetId, imageId, productId):
    args = collections.OrderedDict()
    if isinstance(bannerSetId,GetShopBaseClass): 
      args["bannerSetId"]=json.dumps(bannerSetId.__dict__)
    else:
      try:
        args["bannerSetId"]=json.dumps(bannerSetId)
      except (ValueError, AttributeError):
        args["bannerSetId"]=bannerSetId
    if isinstance(imageId,GetShopBaseClass): 
      args["imageId"]=json.dumps(imageId.__dict__)
    else:
      try:
        args["imageId"]=json.dumps(imageId)
      except (ValueError, AttributeError):
        args["imageId"]=imageId
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    data = EmptyClass()
    data.args = args
    data.method = "linkProductToImage"
    data.interfaceName = "app.banner.IBannerManager"
    return self.communicationHelper.sendMessage(data)

  def removeImage(self, bannerSetId, fileId):
    args = collections.OrderedDict()
    if isinstance(bannerSetId,GetShopBaseClass): 
      args["bannerSetId"]=json.dumps(bannerSetId.__dict__)
    else:
      try:
        args["bannerSetId"]=json.dumps(bannerSetId)
      except (ValueError, AttributeError):
        args["bannerSetId"]=bannerSetId
    if isinstance(fileId,GetShopBaseClass): 
      args["fileId"]=json.dumps(fileId.__dict__)
    else:
      try:
        args["fileId"]=json.dumps(fileId)
      except (ValueError, AttributeError):
        args["fileId"]=fileId
    data = EmptyClass()
    data.args = args
    data.method = "removeImage"
    data.interfaceName = "app.banner.IBannerManager"
    return self.communicationHelper.sendMessage(data)

  def saveSet(self, set):
    args = collections.OrderedDict()
    if isinstance(set,GetShopBaseClass): 
      args["set"]=json.dumps(set.__dict__)
    else:
      try:
        args["set"]=json.dumps(set)
      except (ValueError, AttributeError):
        args["set"]=set
    data = EmptyClass()
    data.args = args
    data.method = "saveSet"
    data.interfaceName = "app.banner.IBannerManager"
    return self.communicationHelper.sendMessage(data)

class BigStock(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addGetShopImageIdToBigStockOrder(self, downloadUrl, imageId):
    args = collections.OrderedDict()
    if isinstance(downloadUrl,GetShopBaseClass): 
      args["downloadUrl"]=json.dumps(downloadUrl.__dict__)
    else:
      try:
        args["downloadUrl"]=json.dumps(downloadUrl)
      except (ValueError, AttributeError):
        args["downloadUrl"]=downloadUrl
    if isinstance(imageId,GetShopBaseClass): 
      args["imageId"]=json.dumps(imageId.__dict__)
    else:
      try:
        args["imageId"]=json.dumps(imageId)
      except (ValueError, AttributeError):
        args["imageId"]=imageId
    data = EmptyClass()
    data.args = args
    data.method = "addGetShopImageIdToBigStockOrder"
    data.interfaceName = "core.bigstock.IBigStock"
    return self.communicationHelper.sendMessage(data)

  def getAvailableCredits(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAvailableCredits"
    data.interfaceName = "core.bigstock.IBigStock"
    return self.communicationHelper.sendMessage(data)

  def purchaseImage(self, imageId, sizeCode):
    args = collections.OrderedDict()
    if isinstance(imageId,GetShopBaseClass): 
      args["imageId"]=json.dumps(imageId.__dict__)
    else:
      try:
        args["imageId"]=json.dumps(imageId)
      except (ValueError, AttributeError):
        args["imageId"]=imageId
    if isinstance(sizeCode,GetShopBaseClass): 
      args["sizeCode"]=json.dumps(sizeCode.__dict__)
    else:
      try:
        args["sizeCode"]=json.dumps(sizeCode)
      except (ValueError, AttributeError):
        args["sizeCode"]=sizeCode
    data = EmptyClass()
    data.args = args
    data.method = "purchaseImage"
    data.interfaceName = "core.bigstock.IBigStock"
    return self.communicationHelper.sendMessage(data)

  def setCreditAccount(self, credits, password):
    args = collections.OrderedDict()
    if isinstance(credits,GetShopBaseClass): 
      args["credits"]=json.dumps(credits.__dict__)
    else:
      try:
        args["credits"]=json.dumps(credits)
      except (ValueError, AttributeError):
        args["credits"]=credits
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "setCreditAccount"
    data.interfaceName = "core.bigstock.IBigStock"
    return self.communicationHelper.sendMessage(data)

class BrainTreeManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def getClientToken(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getClientToken"
    data.interfaceName = "core.braintree.IBrainTreeManager"
    return self.communicationHelper.sendMessage(data)

  def pay(self, paymentMethodNonce, orderId):
    args = collections.OrderedDict()
    if isinstance(paymentMethodNonce,GetShopBaseClass): 
      args["paymentMethodNonce"]=json.dumps(paymentMethodNonce.__dict__)
    else:
      try:
        args["paymentMethodNonce"]=json.dumps(paymentMethodNonce)
      except (ValueError, AttributeError):
        args["paymentMethodNonce"]=paymentMethodNonce
    if isinstance(orderId,GetShopBaseClass): 
      args["orderId"]=json.dumps(orderId.__dict__)
    else:
      try:
        args["orderId"]=json.dumps(orderId)
      except (ValueError, AttributeError):
        args["orderId"]=orderId
    data = EmptyClass()
    data.args = args
    data.method = "pay"
    data.interfaceName = "core.braintree.IBrainTreeManager"
    return self.communicationHelper.sendMessage(data)

class CalendarManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addEvent(self, event):
    args = collections.OrderedDict()
    if isinstance(event,GetShopBaseClass): 
      args["event"]=json.dumps(event.__dict__)
    else:
      try:
        args["event"]=json.dumps(event)
      except (ValueError, AttributeError):
        args["event"]=event
    data = EmptyClass()
    data.args = args
    data.method = "addEvent"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def addUserSilentlyToEvent(self, eventId, userId):
    args = collections.OrderedDict()
    if isinstance(eventId,GetShopBaseClass): 
      args["eventId"]=json.dumps(eventId.__dict__)
    else:
      try:
        args["eventId"]=json.dumps(eventId)
      except (ValueError, AttributeError):
        args["eventId"]=eventId
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    data = EmptyClass()
    data.args = args
    data.method = "addUserSilentlyToEvent"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def addUserToEvent(self, userId, eventId, password, username, source):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(eventId,GetShopBaseClass): 
      args["eventId"]=json.dumps(eventId.__dict__)
    else:
      try:
        args["eventId"]=json.dumps(eventId)
      except (ValueError, AttributeError):
        args["eventId"]=eventId
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    if isinstance(username,GetShopBaseClass): 
      args["username"]=json.dumps(username.__dict__)
    else:
      try:
        args["username"]=json.dumps(username)
      except (ValueError, AttributeError):
        args["username"]=username
    if isinstance(source,GetShopBaseClass): 
      args["source"]=json.dumps(source.__dict__)
    else:
      try:
        args["source"]=json.dumps(source)
      except (ValueError, AttributeError):
        args["source"]=source
    data = EmptyClass()
    data.args = args
    data.method = "addUserToEvent"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def addUserToPageEvent(self, userId, bookingAppId):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(bookingAppId,GetShopBaseClass): 
      args["bookingAppId"]=json.dumps(bookingAppId.__dict__)
    else:
      try:
        args["bookingAppId"]=json.dumps(bookingAppId)
      except (ValueError, AttributeError):
        args["bookingAppId"]=bookingAppId
    data = EmptyClass()
    data.args = args
    data.method = "addUserToPageEvent"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def applyFilter(self, filters):
    args = collections.OrderedDict()
    if isinstance(filters,GetShopBaseClass): 
      args["filters"]=json.dumps(filters.__dict__)
    else:
      try:
        args["filters"]=json.dumps(filters)
      except (ValueError, AttributeError):
        args["filters"]=filters
    data = EmptyClass()
    data.args = args
    data.method = "applyFilter"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def confirmEntry(self, entryId):
    args = collections.OrderedDict()
    if isinstance(entryId,GetShopBaseClass): 
      args["entryId"]=json.dumps(entryId.__dict__)
    else:
      try:
        args["entryId"]=json.dumps(entryId)
      except (ValueError, AttributeError):
        args["entryId"]=entryId
    data = EmptyClass()
    data.args = args
    data.method = "confirmEntry"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def createEntry(self, year, month, day):
    args = collections.OrderedDict()
    if isinstance(year,GetShopBaseClass): 
      args["year"]=json.dumps(year.__dict__)
    else:
      try:
        args["year"]=json.dumps(year)
      except (ValueError, AttributeError):
        args["year"]=year
    if isinstance(month,GetShopBaseClass): 
      args["month"]=json.dumps(month.__dict__)
    else:
      try:
        args["month"]=json.dumps(month)
      except (ValueError, AttributeError):
        args["month"]=month
    if isinstance(day,GetShopBaseClass): 
      args["day"]=json.dumps(day.__dict__)
    else:
      try:
        args["day"]=json.dumps(day)
      except (ValueError, AttributeError):
        args["day"]=day
    data = EmptyClass()
    data.args = args
    data.method = "createEntry"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def deleteEntry(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "deleteEntry"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def deleteLocation(self, locationId):
    args = collections.OrderedDict()
    if isinstance(locationId,GetShopBaseClass): 
      args["locationId"]=json.dumps(locationId.__dict__)
    else:
      try:
        args["locationId"]=json.dumps(locationId)
      except (ValueError, AttributeError):
        args["locationId"]=locationId
    data = EmptyClass()
    data.args = args
    data.method = "deleteLocation"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getActiveFilters(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getActiveFilters"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getAllEventsConnectedToPage(self, pageId):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    data = EmptyClass()
    data.args = args
    data.method = "getAllEventsConnectedToPage"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getAllLocations(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllLocations"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getEntries(self, year, month, day, filters):
    args = collections.OrderedDict()
    if isinstance(year,GetShopBaseClass): 
      args["year"]=json.dumps(year.__dict__)
    else:
      try:
        args["year"]=json.dumps(year)
      except (ValueError, AttributeError):
        args["year"]=year
    if isinstance(month,GetShopBaseClass): 
      args["month"]=json.dumps(month.__dict__)
    else:
      try:
        args["month"]=json.dumps(month)
      except (ValueError, AttributeError):
        args["month"]=month
    if isinstance(day,GetShopBaseClass): 
      args["day"]=json.dumps(day.__dict__)
    else:
      try:
        args["day"]=json.dumps(day)
      except (ValueError, AttributeError):
        args["day"]=day
    if isinstance(filters,GetShopBaseClass): 
      args["filters"]=json.dumps(filters.__dict__)
    else:
      try:
        args["filters"]=json.dumps(filters)
      except (ValueError, AttributeError):
        args["filters"]=filters
    data = EmptyClass()
    data.args = args
    data.method = "getEntries"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getEntriesByUserId(self, userId):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    data = EmptyClass()
    data.args = args
    data.method = "getEntriesByUserId"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getEntry(self, entryId):
    args = collections.OrderedDict()
    if isinstance(entryId,GetShopBaseClass): 
      args["entryId"]=json.dumps(entryId.__dict__)
    else:
      try:
        args["entryId"]=json.dumps(entryId)
      except (ValueError, AttributeError):
        args["entryId"]=entryId
    data = EmptyClass()
    data.args = args
    data.method = "getEntry"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getEvent(self, eventId):
    args = collections.OrderedDict()
    if isinstance(eventId,GetShopBaseClass): 
      args["eventId"]=json.dumps(eventId.__dict__)
    else:
      try:
        args["eventId"]=json.dumps(eventId)
      except (ValueError, AttributeError):
        args["eventId"]=eventId
    data = EmptyClass()
    data.args = args
    data.method = "getEvent"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getEventPartitipatedData(self, pageId):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    data = EmptyClass()
    data.args = args
    data.method = "getEventPartitipatedData"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getEvents(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getEvents"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getEventsGroupedByPageId(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getEventsGroupedByPageId"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getFilters(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getFilters"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getHistory(self, eventId):
    args = collections.OrderedDict()
    if isinstance(eventId,GetShopBaseClass): 
      args["eventId"]=json.dumps(eventId.__dict__)
    else:
      try:
        args["eventId"]=json.dumps(eventId)
      except (ValueError, AttributeError):
        args["eventId"]=eventId
    data = EmptyClass()
    data.args = args
    data.method = "getHistory"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getMonth(self, year, month, includeExtraEvents):
    args = collections.OrderedDict()
    if isinstance(year,GetShopBaseClass): 
      args["year"]=json.dumps(year.__dict__)
    else:
      try:
        args["year"]=json.dumps(year)
      except (ValueError, AttributeError):
        args["year"]=year
    if isinstance(month,GetShopBaseClass): 
      args["month"]=json.dumps(month.__dict__)
    else:
      try:
        args["month"]=json.dumps(month)
      except (ValueError, AttributeError):
        args["month"]=month
    if isinstance(includeExtraEvents,GetShopBaseClass): 
      args["includeExtraEvents"]=json.dumps(includeExtraEvents.__dict__)
    else:
      try:
        args["includeExtraEvents"]=json.dumps(includeExtraEvents)
      except (ValueError, AttributeError):
        args["includeExtraEvents"]=includeExtraEvents
    data = EmptyClass()
    data.args = args
    data.method = "getMonth"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getMonths(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getMonths"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getMonthsAfter(self, year, month):
    args = collections.OrderedDict()
    if isinstance(year,GetShopBaseClass): 
      args["year"]=json.dumps(year.__dict__)
    else:
      try:
        args["year"]=json.dumps(year)
      except (ValueError, AttributeError):
        args["year"]=year
    if isinstance(month,GetShopBaseClass): 
      args["month"]=json.dumps(month.__dict__)
    else:
      try:
        args["month"]=json.dumps(month)
      except (ValueError, AttributeError):
        args["month"]=month
    data = EmptyClass()
    data.args = args
    data.method = "getMonthsAfter"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def getSignature(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getSignature"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def removeUserFromEvent(self, userId, eventId):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(eventId,GetShopBaseClass): 
      args["eventId"]=json.dumps(eventId.__dict__)
    else:
      try:
        args["eventId"]=json.dumps(eventId)
      except (ValueError, AttributeError):
        args["eventId"]=eventId
    data = EmptyClass()
    data.args = args
    data.method = "removeUserFromEvent"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def saveEntry(self, entry):
    args = collections.OrderedDict()
    if isinstance(entry,GetShopBaseClass): 
      args["entry"]=json.dumps(entry.__dict__)
    else:
      try:
        args["entry"]=json.dumps(entry)
      except (ValueError, AttributeError):
        args["entry"]=entry
    data = EmptyClass()
    data.args = args
    data.method = "saveEntry"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def saveLocation(self, location):
    args = collections.OrderedDict()
    if isinstance(location,GetShopBaseClass): 
      args["location"]=json.dumps(location.__dict__)
    else:
      try:
        args["location"]=json.dumps(location)
      except (ValueError, AttributeError):
        args["location"]=location
    data = EmptyClass()
    data.args = args
    data.method = "saveLocation"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def sendReminderToUser(self, byEmail, bySMS, users, text, subject, eventId, attachment, sendReminderToUser):
    args = collections.OrderedDict()
    if isinstance(byEmail,GetShopBaseClass): 
      args["byEmail"]=json.dumps(byEmail.__dict__)
    else:
      try:
        args["byEmail"]=json.dumps(byEmail)
      except (ValueError, AttributeError):
        args["byEmail"]=byEmail
    if isinstance(bySMS,GetShopBaseClass): 
      args["bySMS"]=json.dumps(bySMS.__dict__)
    else:
      try:
        args["bySMS"]=json.dumps(bySMS)
      except (ValueError, AttributeError):
        args["bySMS"]=bySMS
    if isinstance(users,GetShopBaseClass): 
      args["users"]=json.dumps(users.__dict__)
    else:
      try:
        args["users"]=json.dumps(users)
      except (ValueError, AttributeError):
        args["users"]=users
    if isinstance(text,GetShopBaseClass): 
      args["text"]=json.dumps(text.__dict__)
    else:
      try:
        args["text"]=json.dumps(text)
      except (ValueError, AttributeError):
        args["text"]=text
    if isinstance(subject,GetShopBaseClass): 
      args["subject"]=json.dumps(subject.__dict__)
    else:
      try:
        args["subject"]=json.dumps(subject)
      except (ValueError, AttributeError):
        args["subject"]=subject
    if isinstance(eventId,GetShopBaseClass): 
      args["eventId"]=json.dumps(eventId.__dict__)
    else:
      try:
        args["eventId"]=json.dumps(eventId)
      except (ValueError, AttributeError):
        args["eventId"]=eventId
    if isinstance(attachment,GetShopBaseClass): 
      args["attachment"]=json.dumps(attachment.__dict__)
    else:
      try:
        args["attachment"]=json.dumps(attachment)
      except (ValueError, AttributeError):
        args["attachment"]=attachment
    if isinstance(sendReminderToUser,GetShopBaseClass): 
      args["sendReminderToUser"]=json.dumps(sendReminderToUser.__dict__)
    else:
      try:
        args["sendReminderToUser"]=json.dumps(sendReminderToUser)
      except (ValueError, AttributeError):
        args["sendReminderToUser"]=sendReminderToUser
    data = EmptyClass()
    data.args = args
    data.method = "sendReminderToUser"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def setEventPartitipatedData(self, eventData):
    args = collections.OrderedDict()
    if isinstance(eventData,GetShopBaseClass): 
      args["eventData"]=json.dumps(eventData.__dict__)
    else:
      try:
        args["eventData"]=json.dumps(eventData)
      except (ValueError, AttributeError):
        args["eventData"]=eventData
    data = EmptyClass()
    data.args = args
    data.method = "setEventPartitipatedData"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def setSignature(self, userid, signature):
    args = collections.OrderedDict()
    if isinstance(userid,GetShopBaseClass): 
      args["userid"]=json.dumps(userid.__dict__)
    else:
      try:
        args["userid"]=json.dumps(userid)
      except (ValueError, AttributeError):
        args["userid"]=userid
    if isinstance(signature,GetShopBaseClass): 
      args["signature"]=json.dumps(signature.__dict__)
    else:
      try:
        args["signature"]=json.dumps(signature)
      except (ValueError, AttributeError):
        args["signature"]=signature
    data = EmptyClass()
    data.args = args
    data.method = "setSignature"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def transferFromWaitingList(self, entryId, userId):
    args = collections.OrderedDict()
    if isinstance(entryId,GetShopBaseClass): 
      args["entryId"]=json.dumps(entryId.__dict__)
    else:
      try:
        args["entryId"]=json.dumps(entryId)
      except (ValueError, AttributeError):
        args["entryId"]=entryId
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    data = EmptyClass()
    data.args = args
    data.method = "transferFromWaitingList"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

  def transferUser(self, fromEventId, toEventId, userId):
    args = collections.OrderedDict()
    if isinstance(fromEventId,GetShopBaseClass): 
      args["fromEventId"]=json.dumps(fromEventId.__dict__)
    else:
      try:
        args["fromEventId"]=json.dumps(fromEventId)
      except (ValueError, AttributeError):
        args["fromEventId"]=fromEventId
    if isinstance(toEventId,GetShopBaseClass): 
      args["toEventId"]=json.dumps(toEventId.__dict__)
    else:
      try:
        args["toEventId"]=json.dumps(toEventId)
      except (ValueError, AttributeError):
        args["toEventId"]=toEventId
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    data = EmptyClass()
    data.args = args
    data.method = "transferUser"
    data.interfaceName = "core.calendar.ICalendarManager"
    return self.communicationHelper.sendMessage(data)

class CarTuningManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def getCarTuningData(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getCarTuningData"
    data.interfaceName = "core.cartuning.ICarTuningManager"
    return self.communicationHelper.sendMessage(data)

  def saveCarTuningData(self, carList):
    args = collections.OrderedDict()
    if isinstance(carList,GetShopBaseClass): 
      args["carList"]=json.dumps(carList.__dict__)
    else:
      try:
        args["carList"]=json.dumps(carList)
      except (ValueError, AttributeError):
        args["carList"]=carList
    data = EmptyClass()
    data.args = args
    data.method = "saveCarTuningData"
    data.interfaceName = "core.cartuning.ICarTuningManager"
    return self.communicationHelper.sendMessage(data)

class CartManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addCoupon(self, coupon):
    args = collections.OrderedDict()
    if isinstance(coupon,GetShopBaseClass): 
      args["coupon"]=json.dumps(coupon.__dict__)
    else:
      try:
        args["coupon"]=json.dumps(coupon)
      except (ValueError, AttributeError):
        args["coupon"]=coupon
    data = EmptyClass()
    data.args = args
    data.method = "addCoupon"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def addMetaDataToProduct(self, cartItemId, metaData):
    args = collections.OrderedDict()
    if isinstance(cartItemId,GetShopBaseClass): 
      args["cartItemId"]=json.dumps(cartItemId.__dict__)
    else:
      try:
        args["cartItemId"]=json.dumps(cartItemId)
      except (ValueError, AttributeError):
        args["cartItemId"]=cartItemId
    if isinstance(metaData,GetShopBaseClass): 
      args["metaData"]=json.dumps(metaData.__dict__)
    else:
      try:
        args["metaData"]=json.dumps(metaData)
      except (ValueError, AttributeError):
        args["metaData"]=metaData
    data = EmptyClass()
    data.args = args
    data.method = "addMetaDataToProduct"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def addProduct(self, productId, count, variations):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(count,GetShopBaseClass): 
      args["count"]=json.dumps(count.__dict__)
    else:
      try:
        args["count"]=json.dumps(count)
      except (ValueError, AttributeError):
        args["count"]=count
    if isinstance(variations,GetShopBaseClass): 
      args["variations"]=json.dumps(variations.__dict__)
    else:
      try:
        args["variations"]=json.dumps(variations)
      except (ValueError, AttributeError):
        args["variations"]=variations
    data = EmptyClass()
    data.args = args
    data.method = "addProduct"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def addProductItem(self, productId, count):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(count,GetShopBaseClass): 
      args["count"]=json.dumps(count.__dict__)
    else:
      try:
        args["count"]=json.dumps(count)
      except (ValueError, AttributeError):
        args["count"]=count
    data = EmptyClass()
    data.args = args
    data.method = "addProductItem"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def applyCouponToCurrentCart(self, code):
    args = collections.OrderedDict()
    if isinstance(code,GetShopBaseClass): 
      args["code"]=json.dumps(code.__dict__)
    else:
      try:
        args["code"]=json.dumps(code)
      except (ValueError, AttributeError):
        args["code"]=code
    data = EmptyClass()
    data.args = args
    data.method = "applyCouponToCurrentCart"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def calculateTotalCost(self, cart):
    args = collections.OrderedDict()
    if isinstance(cart,GetShopBaseClass): 
      args["cart"]=json.dumps(cart.__dict__)
    else:
      try:
        args["cart"]=json.dumps(cart)
      except (ValueError, AttributeError):
        args["cart"]=cart
    data = EmptyClass()
    data.args = args
    data.method = "calculateTotalCost"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def calculateTotalCount(self, cart):
    args = collections.OrderedDict()
    if isinstance(cart,GetShopBaseClass): 
      args["cart"]=json.dumps(cart.__dict__)
    else:
      try:
        args["cart"]=json.dumps(cart)
      except (ValueError, AttributeError):
        args["cart"]=cart
    data = EmptyClass()
    data.args = args
    data.method = "calculateTotalCount"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def clear(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "clear"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def getCart(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getCart"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def getCartTotalAmount(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getCartTotalAmount"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def getCoupons(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getCoupons"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def getShippingCost(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getShippingCost"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def getShippingPriceBasis(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getShippingPriceBasis"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def getTaxes(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getTaxes"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def removeAllCoupons(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "removeAllCoupons"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def removeCoupon(self, code):
    args = collections.OrderedDict()
    if isinstance(code,GetShopBaseClass): 
      args["code"]=json.dumps(code.__dict__)
    else:
      try:
        args["code"]=json.dumps(code)
      except (ValueError, AttributeError):
        args["code"]=code
    data = EmptyClass()
    data.args = args
    data.method = "removeCoupon"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def removeProduct(self, cartItemId):
    args = collections.OrderedDict()
    if isinstance(cartItemId,GetShopBaseClass): 
      args["cartItemId"]=json.dumps(cartItemId.__dict__)
    else:
      try:
        args["cartItemId"]=json.dumps(cartItemId)
      except (ValueError, AttributeError):
        args["cartItemId"]=cartItemId
    data = EmptyClass()
    data.args = args
    data.method = "removeProduct"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def setAddress(self, address):
    args = collections.OrderedDict()
    if isinstance(address,GetShopBaseClass): 
      args["address"]=json.dumps(address.__dict__)
    else:
      try:
        args["address"]=json.dumps(address)
      except (ValueError, AttributeError):
        args["address"]=address
    data = EmptyClass()
    data.args = args
    data.method = "setAddress"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def setReference(self, reference):
    args = collections.OrderedDict()
    if isinstance(reference,GetShopBaseClass): 
      args["reference"]=json.dumps(reference.__dict__)
    else:
      try:
        args["reference"]=json.dumps(reference)
      except (ValueError, AttributeError):
        args["reference"]=reference
    data = EmptyClass()
    data.args = args
    data.method = "setReference"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def setShippingCost(self, shippingCost):
    args = collections.OrderedDict()
    if isinstance(shippingCost,GetShopBaseClass): 
      args["shippingCost"]=json.dumps(shippingCost.__dict__)
    else:
      try:
        args["shippingCost"]=json.dumps(shippingCost)
      except (ValueError, AttributeError):
        args["shippingCost"]=shippingCost
    data = EmptyClass()
    data.args = args
    data.method = "setShippingCost"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

  def updateProductCount(self, cartItemId, count):
    args = collections.OrderedDict()
    if isinstance(cartItemId,GetShopBaseClass): 
      args["cartItemId"]=json.dumps(cartItemId.__dict__)
    else:
      try:
        args["cartItemId"]=json.dumps(cartItemId)
      except (ValueError, AttributeError):
        args["cartItemId"]=cartItemId
    if isinstance(count,GetShopBaseClass): 
      args["count"]=json.dumps(count.__dict__)
    else:
      try:
        args["count"]=json.dumps(count)
      except (ValueError, AttributeError):
        args["count"]=count
    data = EmptyClass()
    data.args = args
    data.method = "updateProductCount"
    data.interfaceName = "core.cartmanager.ICartManager"
    return self.communicationHelper.sendMessage(data)

class ChatManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def closeChat(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "closeChat"
    data.interfaceName = "core.chat.IChatManager"
    return self.communicationHelper.sendMessage(data)

  def getChatters(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getChatters"
    data.interfaceName = "core.chat.IChatManager"
    return self.communicationHelper.sendMessage(data)

  def getMessages(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getMessages"
    data.interfaceName = "core.chat.IChatManager"
    return self.communicationHelper.sendMessage(data)

  def pingMobileChat(self, chatterid):
    args = collections.OrderedDict()
    if isinstance(chatterid,GetShopBaseClass): 
      args["chatterid"]=json.dumps(chatterid.__dict__)
    else:
      try:
        args["chatterid"]=json.dumps(chatterid)
      except (ValueError, AttributeError):
        args["chatterid"]=chatterid
    data = EmptyClass()
    data.args = args
    data.method = "pingMobileChat"
    data.interfaceName = "core.chat.IChatManager"
    return self.communicationHelper.sendMessage(data)

  def replyToChat(self, id, message):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    if isinstance(message,GetShopBaseClass): 
      args["message"]=json.dumps(message.__dict__)
    else:
      try:
        args["message"]=json.dumps(message)
      except (ValueError, AttributeError):
        args["message"]=message
    data = EmptyClass()
    data.args = args
    data.method = "replyToChat"
    data.interfaceName = "core.chat.IChatManager"
    return self.communicationHelper.sendMessage(data)

  def sendMessage(self, message):
    args = collections.OrderedDict()
    if isinstance(message,GetShopBaseClass): 
      args["message"]=json.dumps(message.__dict__)
    else:
      try:
        args["message"]=json.dumps(message)
      except (ValueError, AttributeError):
        args["message"]=message
    data = EmptyClass()
    data.args = args
    data.method = "sendMessage"
    data.interfaceName = "core.chat.IChatManager"
    return self.communicationHelper.sendMessage(data)

class ContentManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def createContent(self, content):
    args = collections.OrderedDict()
    if isinstance(content,GetShopBaseClass): 
      args["content"]=json.dumps(content.__dict__)
    else:
      try:
        args["content"]=json.dumps(content)
      except (ValueError, AttributeError):
        args["content"]=content
    data = EmptyClass()
    data.args = args
    data.method = "createContent"
    data.interfaceName = "app.content.IContentManager"
    return self.communicationHelper.sendMessage(data)

  def deleteContent(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "deleteContent"
    data.interfaceName = "app.content.IContentManager"
    return self.communicationHelper.sendMessage(data)

  def getAllContent(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllContent"
    data.interfaceName = "app.content.IContentManager"
    return self.communicationHelper.sendMessage(data)

  def getContent(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getContent"
    data.interfaceName = "app.content.IContentManager"
    return self.communicationHelper.sendMessage(data)

  def saveContent(self, id, content):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    if isinstance(content,GetShopBaseClass): 
      args["content"]=json.dumps(content.__dict__)
    else:
      try:
        args["content"]=json.dumps(content)
      except (ValueError, AttributeError):
        args["content"]=content
    data = EmptyClass()
    data.args = args
    data.method = "saveContent"
    data.interfaceName = "app.content.IContentManager"
    return self.communicationHelper.sendMessage(data)

class FooterManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def getConfiguration(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getConfiguration"
    data.interfaceName = "app.footer.IFooterManager"
    return self.communicationHelper.sendMessage(data)

  def setLayout(self, numberOfColumns):
    args = collections.OrderedDict()
    if isinstance(numberOfColumns,GetShopBaseClass): 
      args["numberOfColumns"]=json.dumps(numberOfColumns.__dict__)
    else:
      try:
        args["numberOfColumns"]=json.dumps(numberOfColumns)
      except (ValueError, AttributeError):
        args["numberOfColumns"]=numberOfColumns
    data = EmptyClass()
    data.args = args
    data.method = "setLayout"
    data.interfaceName = "app.footer.IFooterManager"
    return self.communicationHelper.sendMessage(data)

  def setType(self, column, type):
    args = collections.OrderedDict()
    if isinstance(column,GetShopBaseClass): 
      args["column"]=json.dumps(column.__dict__)
    else:
      try:
        args["column"]=json.dumps(column)
      except (ValueError, AttributeError):
        args["column"]=column
    if isinstance(type,GetShopBaseClass): 
      args["type"]=json.dumps(type.__dict__)
    else:
      try:
        args["type"]=json.dumps(type)
      except (ValueError, AttributeError):
        args["type"]=type
    data = EmptyClass()
    data.args = args
    data.method = "setType"
    data.interfaceName = "app.footer.IFooterManager"
    return self.communicationHelper.sendMessage(data)

class GalleryManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addImageToGallery(self, galleryId, imageId, description, title):
    args = collections.OrderedDict()
    if isinstance(galleryId,GetShopBaseClass): 
      args["galleryId"]=json.dumps(galleryId.__dict__)
    else:
      try:
        args["galleryId"]=json.dumps(galleryId)
      except (ValueError, AttributeError):
        args["galleryId"]=galleryId
    if isinstance(imageId,GetShopBaseClass): 
      args["imageId"]=json.dumps(imageId.__dict__)
    else:
      try:
        args["imageId"]=json.dumps(imageId)
      except (ValueError, AttributeError):
        args["imageId"]=imageId
    if isinstance(description,GetShopBaseClass): 
      args["description"]=json.dumps(description.__dict__)
    else:
      try:
        args["description"]=json.dumps(description)
      except (ValueError, AttributeError):
        args["description"]=description
    if isinstance(title,GetShopBaseClass): 
      args["title"]=json.dumps(title.__dict__)
    else:
      try:
        args["title"]=json.dumps(title)
      except (ValueError, AttributeError):
        args["title"]=title
    data = EmptyClass()
    data.args = args
    data.method = "addImageToGallery"
    data.interfaceName = "core.gallerymanager.IGalleryManager"
    return self.communicationHelper.sendMessage(data)

  def createImageGallery(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "createImageGallery"
    data.interfaceName = "core.gallerymanager.IGalleryManager"
    return self.communicationHelper.sendMessage(data)

  def deleteImage(self, entryId):
    args = collections.OrderedDict()
    if isinstance(entryId,GetShopBaseClass): 
      args["entryId"]=json.dumps(entryId.__dict__)
    else:
      try:
        args["entryId"]=json.dumps(entryId)
      except (ValueError, AttributeError):
        args["entryId"]=entryId
    data = EmptyClass()
    data.args = args
    data.method = "deleteImage"
    data.interfaceName = "core.gallerymanager.IGalleryManager"
    return self.communicationHelper.sendMessage(data)

  def getAllImages(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getAllImages"
    data.interfaceName = "core.gallerymanager.IGalleryManager"
    return self.communicationHelper.sendMessage(data)

  def getEntry(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getEntry"
    data.interfaceName = "core.gallerymanager.IGalleryManager"
    return self.communicationHelper.sendMessage(data)

  def saveEntry(self, entry):
    args = collections.OrderedDict()
    if isinstance(entry,GetShopBaseClass): 
      args["entry"]=json.dumps(entry.__dict__)
    else:
      try:
        args["entry"]=json.dumps(entry)
      except (ValueError, AttributeError):
        args["entry"]=entry
    data = EmptyClass()
    data.args = args
    data.method = "saveEntry"
    data.interfaceName = "core.gallerymanager.IGalleryManager"
    return self.communicationHelper.sendMessage(data)

class GetShop(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addUserToPartner(self, userId, partner, password):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(partner,GetShopBaseClass): 
      args["partner"]=json.dumps(partner.__dict__)
    else:
      try:
        args["partner"]=json.dumps(partner)
      except (ValueError, AttributeError):
        args["partner"]=partner
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "addUserToPartner"
    data.interfaceName = "core.getshop.IGetShop"
    return self.communicationHelper.sendMessage(data)

  def createWebPage(self, webpageData):
    args = collections.OrderedDict()
    if isinstance(webpageData,GetShopBaseClass): 
      args["webpageData"]=json.dumps(webpageData.__dict__)
    else:
      try:
        args["webpageData"]=json.dumps(webpageData)
      except (ValueError, AttributeError):
        args["webpageData"]=webpageData
    data = EmptyClass()
    data.args = args
    data.method = "createWebPage"
    data.interfaceName = "core.getshop.IGetShop"
    return self.communicationHelper.sendMessage(data)

  def findAddressForApplication(self, uuid):
    args = collections.OrderedDict()
    if isinstance(uuid,GetShopBaseClass): 
      args["uuid"]=json.dumps(uuid.__dict__)
    else:
      try:
        args["uuid"]=json.dumps(uuid)
      except (ValueError, AttributeError):
        args["uuid"]=uuid
    data = EmptyClass()
    data.args = args
    data.method = "findAddressForApplication"
    data.interfaceName = "core.getshop.IGetShop"
    return self.communicationHelper.sendMessage(data)

  def findAddressForUUID(self, uuid):
    args = collections.OrderedDict()
    if isinstance(uuid,GetShopBaseClass): 
      args["uuid"]=json.dumps(uuid.__dict__)
    else:
      try:
        args["uuid"]=json.dumps(uuid)
      except (ValueError, AttributeError):
        args["uuid"]=uuid
    data = EmptyClass()
    data.args = args
    data.method = "findAddressForUUID"
    data.interfaceName = "core.getshop.IGetShop"
    return self.communicationHelper.sendMessage(data)

  def getPartnerData(self, partnerId, password):
    args = collections.OrderedDict()
    if isinstance(partnerId,GetShopBaseClass): 
      args["partnerId"]=json.dumps(partnerId.__dict__)
    else:
      try:
        args["partnerId"]=json.dumps(partnerId)
      except (ValueError, AttributeError):
        args["partnerId"]=partnerId
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "getPartnerData"
    data.interfaceName = "core.getshop.IGetShop"
    return self.communicationHelper.sendMessage(data)

  def getStores(self, code):
    args = collections.OrderedDict()
    if isinstance(code,GetShopBaseClass): 
      args["code"]=json.dumps(code.__dict__)
    else:
      try:
        args["code"]=json.dumps(code)
      except (ValueError, AttributeError):
        args["code"]=code
    data = EmptyClass()
    data.args = args
    data.method = "getStores"
    data.interfaceName = "core.getshop.IGetShop"
    return self.communicationHelper.sendMessage(data)

  def saveSmsCallback(self, smsResponses):
    args = collections.OrderedDict()
    if isinstance(smsResponses,GetShopBaseClass): 
      args["smsResponses"]=json.dumps(smsResponses.__dict__)
    else:
      try:
        args["smsResponses"]=json.dumps(smsResponses)
      except (ValueError, AttributeError):
        args["smsResponses"]=smsResponses
    data = EmptyClass()
    data.args = args
    data.method = "saveSmsCallback"
    data.interfaceName = "core.getshop.IGetShop"
    return self.communicationHelper.sendMessage(data)

  def setApplicationList(self, ids, partnerId, password):
    args = collections.OrderedDict()
    if isinstance(ids,GetShopBaseClass): 
      args["ids"]=json.dumps(ids.__dict__)
    else:
      try:
        args["ids"]=json.dumps(ids)
      except (ValueError, AttributeError):
        args["ids"]=ids
    if isinstance(partnerId,GetShopBaseClass): 
      args["partnerId"]=json.dumps(partnerId.__dict__)
    else:
      try:
        args["partnerId"]=json.dumps(partnerId)
      except (ValueError, AttributeError):
        args["partnerId"]=partnerId
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "setApplicationList"
    data.interfaceName = "core.getshop.IGetShop"
    return self.communicationHelper.sendMessage(data)

  def startStoreFromStore(self, startData):
    args = collections.OrderedDict()
    if isinstance(startData,GetShopBaseClass): 
      args["startData"]=json.dumps(startData.__dict__)
    else:
      try:
        args["startData"]=json.dumps(startData)
      except (ValueError, AttributeError):
        args["startData"]=startData
    data = EmptyClass()
    data.args = args
    data.method = "startStoreFromStore"
    data.interfaceName = "core.getshop.IGetShop"
    return self.communicationHelper.sendMessage(data)

class GetShopApplicationPool(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def deleteApplication(self, applicationId):
    args = collections.OrderedDict()
    if isinstance(applicationId,GetShopBaseClass): 
      args["applicationId"]=json.dumps(applicationId.__dict__)
    else:
      try:
        args["applicationId"]=json.dumps(applicationId)
      except (ValueError, AttributeError):
        args["applicationId"]=applicationId
    data = EmptyClass()
    data.args = args
    data.method = "deleteApplication"
    data.interfaceName = "core.applications.IGetShopApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def get(self, applicationId):
    args = collections.OrderedDict()
    if isinstance(applicationId,GetShopBaseClass): 
      args["applicationId"]=json.dumps(applicationId.__dict__)
    else:
      try:
        args["applicationId"]=json.dumps(applicationId)
      except (ValueError, AttributeError):
        args["applicationId"]=applicationId
    data = EmptyClass()
    data.args = args
    data.method = "get"
    data.interfaceName = "core.applications.IGetShopApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def getApplications(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getApplications"
    data.interfaceName = "core.applications.IGetShopApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def saveApplication(self, application):
    args = collections.OrderedDict()
    if isinstance(application,GetShopBaseClass): 
      args["application"]=json.dumps(application.__dict__)
    else:
      try:
        args["application"]=json.dumps(application)
      except (ValueError, AttributeError):
        args["application"]=application
    data = EmptyClass()
    data.args = args
    data.method = "saveApplication"
    data.interfaceName = "core.applications.IGetShopApplicationPool"
    return self.communicationHelper.sendMessage(data)

class HotelBookingManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def createDomain(self, name, description):
    args = collections.OrderedDict()
    if isinstance(name,GetShopBaseClass): 
      args["name"]=json.dumps(name.__dict__)
    else:
      try:
        args["name"]=json.dumps(name)
      except (ValueError, AttributeError):
        args["name"]=name
    if isinstance(description,GetShopBaseClass): 
      args["description"]=json.dumps(description.__dict__)
    else:
      try:
        args["description"]=json.dumps(description)
      except (ValueError, AttributeError):
        args["description"]=description
    data = EmptyClass()
    data.args = args
    data.method = "createDomain"
    data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager"
    return self.communicationHelper.sendMessage(data)

  def createRoomType(self, domainId, name, price, size):
    args = collections.OrderedDict()
    if isinstance(domainId,GetShopBaseClass): 
      args["domainId"]=json.dumps(domainId.__dict__)
    else:
      try:
        args["domainId"]=json.dumps(domainId)
      except (ValueError, AttributeError):
        args["domainId"]=domainId
    if isinstance(name,GetShopBaseClass): 
      args["name"]=json.dumps(name.__dict__)
    else:
      try:
        args["name"]=json.dumps(name)
      except (ValueError, AttributeError):
        args["name"]=name
    if isinstance(price,GetShopBaseClass): 
      args["price"]=json.dumps(price.__dict__)
    else:
      try:
        args["price"]=json.dumps(price)
      except (ValueError, AttributeError):
        args["price"]=price
    if isinstance(size,GetShopBaseClass): 
      args["size"]=json.dumps(size.__dict__)
    else:
      try:
        args["size"]=json.dumps(size)
      except (ValueError, AttributeError):
        args["size"]=size
    data = EmptyClass()
    data.args = args
    data.method = "createRoomType"
    data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager"
    return self.communicationHelper.sendMessage(data)

  def deleteDomain(self, domainId):
    args = collections.OrderedDict()
    if isinstance(domainId,GetShopBaseClass): 
      args["domainId"]=json.dumps(domainId.__dict__)
    else:
      try:
        args["domainId"]=json.dumps(domainId)
      except (ValueError, AttributeError):
        args["domainId"]=domainId
    data = EmptyClass()
    data.args = args
    data.method = "deleteDomain"
    data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager"
    return self.communicationHelper.sendMessage(data)

  def deleteRoom(self, domainId, roomId):
    args = collections.OrderedDict()
    if isinstance(domainId,GetShopBaseClass): 
      args["domainId"]=json.dumps(domainId.__dict__)
    else:
      try:
        args["domainId"]=json.dumps(domainId)
      except (ValueError, AttributeError):
        args["domainId"]=domainId
    if isinstance(roomId,GetShopBaseClass): 
      args["roomId"]=json.dumps(roomId.__dict__)
    else:
      try:
        args["roomId"]=json.dumps(roomId)
      except (ValueError, AttributeError):
        args["roomId"]=roomId
    data = EmptyClass()
    data.args = args
    data.method = "deleteRoom"
    data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager"
    return self.communicationHelper.sendMessage(data)

  def deleteRoomType(self, domainId, roomTypeId):
    args = collections.OrderedDict()
    if isinstance(domainId,GetShopBaseClass): 
      args["domainId"]=json.dumps(domainId.__dict__)
    else:
      try:
        args["domainId"]=json.dumps(domainId)
      except (ValueError, AttributeError):
        args["domainId"]=domainId
    if isinstance(roomTypeId,GetShopBaseClass): 
      args["roomTypeId"]=json.dumps(roomTypeId.__dict__)
    else:
      try:
        args["roomTypeId"]=json.dumps(roomTypeId)
      except (ValueError, AttributeError):
        args["roomTypeId"]=roomTypeId
    data = EmptyClass()
    data.args = args
    data.method = "deleteRoomType"
    data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager"
    return self.communicationHelper.sendMessage(data)

  def getDomain(self, domainId):
    args = collections.OrderedDict()
    if isinstance(domainId,GetShopBaseClass): 
      args["domainId"]=json.dumps(domainId.__dict__)
    else:
      try:
        args["domainId"]=json.dumps(domainId)
      except (ValueError, AttributeError):
        args["domainId"]=domainId
    data = EmptyClass()
    data.args = args
    data.method = "getDomain"
    data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager"
    return self.communicationHelper.sendMessage(data)

  def getDomains(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getDomains"
    data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager"
    return self.communicationHelper.sendMessage(data)

  def getRoom(self, domainId, roomId):
    args = collections.OrderedDict()
    if isinstance(domainId,GetShopBaseClass): 
      args["domainId"]=json.dumps(domainId.__dict__)
    else:
      try:
        args["domainId"]=json.dumps(domainId)
      except (ValueError, AttributeError):
        args["domainId"]=domainId
    if isinstance(roomId,GetShopBaseClass): 
      args["roomId"]=json.dumps(roomId.__dict__)
    else:
      try:
        args["roomId"]=json.dumps(roomId)
      except (ValueError, AttributeError):
        args["roomId"]=roomId
    data = EmptyClass()
    data.args = args
    data.method = "getRoom"
    data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager"
    return self.communicationHelper.sendMessage(data)

  def getRoomType(self, domainId, roomTypeId):
    args = collections.OrderedDict()
    if isinstance(domainId,GetShopBaseClass): 
      args["domainId"]=json.dumps(domainId.__dict__)
    else:
      try:
        args["domainId"]=json.dumps(domainId)
      except (ValueError, AttributeError):
        args["domainId"]=domainId
    if isinstance(roomTypeId,GetShopBaseClass): 
      args["roomTypeId"]=json.dumps(roomTypeId.__dict__)
    else:
      try:
        args["roomTypeId"]=json.dumps(roomTypeId)
      except (ValueError, AttributeError):
        args["roomTypeId"]=roomTypeId
    data = EmptyClass()
    data.args = args
    data.method = "getRoomType"
    data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager"
    return self.communicationHelper.sendMessage(data)

  def getRoomTypes(self, domainId):
    args = collections.OrderedDict()
    if isinstance(domainId,GetShopBaseClass): 
      args["domainId"]=json.dumps(domainId.__dict__)
    else:
      try:
        args["domainId"]=json.dumps(domainId)
      except (ValueError, AttributeError):
        args["domainId"]=domainId
    data = EmptyClass()
    data.args = args
    data.method = "getRoomTypes"
    data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager"
    return self.communicationHelper.sendMessage(data)

  def getRooms(self, domainId):
    args = collections.OrderedDict()
    if isinstance(domainId,GetShopBaseClass): 
      args["domainId"]=json.dumps(domainId.__dict__)
    else:
      try:
        args["domainId"]=json.dumps(domainId)
      except (ValueError, AttributeError):
        args["domainId"]=domainId
    data = EmptyClass()
    data.args = args
    data.method = "getRooms"
    data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager"
    return self.communicationHelper.sendMessage(data)

  def saveRoom(self, room):
    args = collections.OrderedDict()
    if isinstance(room,GetShopBaseClass): 
      args["room"]=json.dumps(room.__dict__)
    else:
      try:
        args["room"]=json.dumps(room)
      except (ValueError, AttributeError):
        args["room"]=room
    data = EmptyClass()
    data.args = args
    data.method = "saveRoom"
    data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager"
    return self.communicationHelper.sendMessage(data)

class InformationScreenManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addSlider(self, slider, tvId):
    args = collections.OrderedDict()
    if isinstance(slider,GetShopBaseClass): 
      args["slider"]=json.dumps(slider.__dict__)
    else:
      try:
        args["slider"]=json.dumps(slider)
      except (ValueError, AttributeError):
        args["slider"]=slider
    if isinstance(tvId,GetShopBaseClass): 
      args["tvId"]=json.dumps(tvId.__dict__)
    else:
      try:
        args["tvId"]=json.dumps(tvId)
      except (ValueError, AttributeError):
        args["tvId"]=tvId
    data = EmptyClass()
    data.args = args
    data.method = "addSlider"
    data.interfaceName = "core.informationscreenmanager.IInformationScreenManager"
    return self.communicationHelper.sendMessage(data)

  def deleteSlider(self, sliderId, tvId):
    args = collections.OrderedDict()
    if isinstance(sliderId,GetShopBaseClass): 
      args["sliderId"]=json.dumps(sliderId.__dict__)
    else:
      try:
        args["sliderId"]=json.dumps(sliderId)
      except (ValueError, AttributeError):
        args["sliderId"]=sliderId
    if isinstance(tvId,GetShopBaseClass): 
      args["tvId"]=json.dumps(tvId.__dict__)
    else:
      try:
        args["tvId"]=json.dumps(tvId)
      except (ValueError, AttributeError):
        args["tvId"]=tvId
    data = EmptyClass()
    data.args = args
    data.method = "deleteSlider"
    data.interfaceName = "core.informationscreenmanager.IInformationScreenManager"
    return self.communicationHelper.sendMessage(data)

  def getHolders(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getHolders"
    data.interfaceName = "core.informationscreenmanager.IInformationScreenManager"
    return self.communicationHelper.sendMessage(data)

  def getInformationScreens(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getInformationScreens"
    data.interfaceName = "core.informationscreenmanager.IInformationScreenManager"
    return self.communicationHelper.sendMessage(data)

  def getNews(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getNews"
    data.interfaceName = "core.informationscreenmanager.IInformationScreenManager"
    return self.communicationHelper.sendMessage(data)

  def getScreen(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getScreen"
    data.interfaceName = "core.informationscreenmanager.IInformationScreenManager"
    return self.communicationHelper.sendMessage(data)

  def getTypes(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getTypes"
    data.interfaceName = "core.informationscreenmanager.IInformationScreenManager"
    return self.communicationHelper.sendMessage(data)

  def registerTv(self, customerId):
    args = collections.OrderedDict()
    if isinstance(customerId,GetShopBaseClass): 
      args["customerId"]=json.dumps(customerId.__dict__)
    else:
      try:
        args["customerId"]=json.dumps(customerId)
      except (ValueError, AttributeError):
        args["customerId"]=customerId
    data = EmptyClass()
    data.args = args
    data.method = "registerTv"
    data.interfaceName = "core.informationscreenmanager.IInformationScreenManager"
    return self.communicationHelper.sendMessage(data)

  def saveTv(self, tv):
    args = collections.OrderedDict()
    if isinstance(tv,GetShopBaseClass): 
      args["tv"]=json.dumps(tv.__dict__)
    else:
      try:
        args["tv"]=json.dumps(tv)
      except (ValueError, AttributeError):
        args["tv"]=tv
    data = EmptyClass()
    data.args = args
    data.method = "saveTv"
    data.interfaceName = "core.informationscreenmanager.IInformationScreenManager"
    return self.communicationHelper.sendMessage(data)

class InvoiceManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def createInvoice(self, orderId):
    args = collections.OrderedDict()
    if isinstance(orderId,GetShopBaseClass): 
      args["orderId"]=json.dumps(orderId.__dict__)
    else:
      try:
        args["orderId"]=json.dumps(orderId)
      except (ValueError, AttributeError):
        args["orderId"]=orderId
    data = EmptyClass()
    data.args = args
    data.method = "createInvoice"
    data.interfaceName = "core.pdf.IInvoiceManager"
    return self.communicationHelper.sendMessage(data)

  def getBase64EncodedInvoice(self, orderId):
    args = collections.OrderedDict()
    if isinstance(orderId,GetShopBaseClass): 
      args["orderId"]=json.dumps(orderId.__dict__)
    else:
      try:
        args["orderId"]=json.dumps(orderId)
      except (ValueError, AttributeError):
        args["orderId"]=orderId
    data = EmptyClass()
    data.args = args
    data.method = "getBase64EncodedInvoice"
    data.interfaceName = "core.pdf.IInvoiceManager"
    return self.communicationHelper.sendMessage(data)

class LasGruppenPDFGenerator(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def generatePdf(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "generatePdf"
    data.interfaceName = "core.pdf.ILasGruppenPDFGenerator"
    return self.communicationHelper.sendMessage(data)

class ListManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addEntry(self, listId, entry, parentPageId):
    args = collections.OrderedDict()
    if isinstance(listId,GetShopBaseClass): 
      args["listId"]=json.dumps(listId.__dict__)
    else:
      try:
        args["listId"]=json.dumps(listId)
      except (ValueError, AttributeError):
        args["listId"]=listId
    if isinstance(entry,GetShopBaseClass): 
      args["entry"]=json.dumps(entry.__dict__)
    else:
      try:
        args["entry"]=json.dumps(entry)
      except (ValueError, AttributeError):
        args["entry"]=entry
    if isinstance(parentPageId,GetShopBaseClass): 
      args["parentPageId"]=json.dumps(parentPageId.__dict__)
    else:
      try:
        args["parentPageId"]=json.dumps(parentPageId)
      except (ValueError, AttributeError):
        args["parentPageId"]=parentPageId
    data = EmptyClass()
    data.args = args
    data.method = "addEntry"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def clearList(self, listId):
    args = collections.OrderedDict()
    if isinstance(listId,GetShopBaseClass): 
      args["listId"]=json.dumps(listId.__dict__)
    else:
      try:
        args["listId"]=json.dumps(listId)
      except (ValueError, AttributeError):
        args["listId"]=listId
    data = EmptyClass()
    data.args = args
    data.method = "clearList"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def combineList(self, toListId, newListId):
    args = collections.OrderedDict()
    if isinstance(toListId,GetShopBaseClass): 
      args["toListId"]=json.dumps(toListId.__dict__)
    else:
      try:
        args["toListId"]=json.dumps(toListId)
      except (ValueError, AttributeError):
        args["toListId"]=toListId
    if isinstance(newListId,GetShopBaseClass): 
      args["newListId"]=json.dumps(newListId.__dict__)
    else:
      try:
        args["newListId"]=json.dumps(newListId)
      except (ValueError, AttributeError):
        args["newListId"]=newListId
    data = EmptyClass()
    data.args = args
    data.method = "combineList"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def createListId(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "createListId"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def createMenuList(self, menuApplicationId):
    args = collections.OrderedDict()
    if isinstance(menuApplicationId,GetShopBaseClass): 
      args["menuApplicationId"]=json.dumps(menuApplicationId.__dict__)
    else:
      try:
        args["menuApplicationId"]=json.dumps(menuApplicationId)
      except (ValueError, AttributeError):
        args["menuApplicationId"]=menuApplicationId
    data = EmptyClass()
    data.args = args
    data.method = "createMenuList"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def deleteEntry(self, id, listId):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    if isinstance(listId,GetShopBaseClass): 
      args["listId"]=json.dumps(listId.__dict__)
    else:
      try:
        args["listId"]=json.dumps(listId)
      except (ValueError, AttributeError):
        args["listId"]=listId
    data = EmptyClass()
    data.args = args
    data.method = "deleteEntry"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def getAllListsByType(self, type):
    args = collections.OrderedDict()
    if isinstance(type,GetShopBaseClass): 
      args["type"]=json.dumps(type.__dict__)
    else:
      try:
        args["type"]=json.dumps(type)
      except (ValueError, AttributeError):
        args["type"]=type
    data = EmptyClass()
    data.args = args
    data.method = "getAllListsByType"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def getCombinedLists(self, listId):
    args = collections.OrderedDict()
    if isinstance(listId,GetShopBaseClass): 
      args["listId"]=json.dumps(listId.__dict__)
    else:
      try:
        args["listId"]=json.dumps(listId)
      except (ValueError, AttributeError):
        args["listId"]=listId
    data = EmptyClass()
    data.args = args
    data.method = "getCombinedLists"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def getList(self, listId):
    args = collections.OrderedDict()
    if isinstance(listId,GetShopBaseClass): 
      args["listId"]=json.dumps(listId.__dict__)
    else:
      try:
        args["listId"]=json.dumps(listId)
      except (ValueError, AttributeError):
        args["listId"]=listId
    data = EmptyClass()
    data.args = args
    data.method = "getList"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def getListEntry(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getListEntry"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def getLists(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getLists"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def getPageIdByName(self, name):
    args = collections.OrderedDict()
    if isinstance(name,GetShopBaseClass): 
      args["name"]=json.dumps(name.__dict__)
    else:
      try:
        args["name"]=json.dumps(name)
      except (ValueError, AttributeError):
        args["name"]=name
    data = EmptyClass()
    data.args = args
    data.method = "getPageIdByName"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def orderEntry(self, id, after, parentId):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    if isinstance(after,GetShopBaseClass): 
      args["after"]=json.dumps(after.__dict__)
    else:
      try:
        args["after"]=json.dumps(after)
      except (ValueError, AttributeError):
        args["after"]=after
    if isinstance(parentId,GetShopBaseClass): 
      args["parentId"]=json.dumps(parentId.__dict__)
    else:
      try:
        args["parentId"]=json.dumps(parentId)
      except (ValueError, AttributeError):
        args["parentId"]=parentId
    data = EmptyClass()
    data.args = args
    data.method = "orderEntry"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def setEntries(self, listId, entries):
    args = collections.OrderedDict()
    if isinstance(listId,GetShopBaseClass): 
      args["listId"]=json.dumps(listId.__dict__)
    else:
      try:
        args["listId"]=json.dumps(listId)
      except (ValueError, AttributeError):
        args["listId"]=listId
    if isinstance(entries,GetShopBaseClass): 
      args["entries"]=json.dumps(entries.__dict__)
    else:
      try:
        args["entries"]=json.dumps(entries)
      except (ValueError, AttributeError):
        args["entries"]=entries
    data = EmptyClass()
    data.args = args
    data.method = "setEntries"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def translateEntries(self, entryIds):
    args = collections.OrderedDict()
    if isinstance(entryIds,GetShopBaseClass): 
      args["entryIds"]=json.dumps(entryIds.__dict__)
    else:
      try:
        args["entryIds"]=json.dumps(entryIds)
      except (ValueError, AttributeError):
        args["entryIds"]=entryIds
    data = EmptyClass()
    data.args = args
    data.method = "translateEntries"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def unCombineList(self, fromListId, toRemoveId):
    args = collections.OrderedDict()
    if isinstance(fromListId,GetShopBaseClass): 
      args["fromListId"]=json.dumps(fromListId.__dict__)
    else:
      try:
        args["fromListId"]=json.dumps(fromListId)
      except (ValueError, AttributeError):
        args["fromListId"]=fromListId
    if isinstance(toRemoveId,GetShopBaseClass): 
      args["toRemoveId"]=json.dumps(toRemoveId.__dict__)
    else:
      try:
        args["toRemoveId"]=json.dumps(toRemoveId)
      except (ValueError, AttributeError):
        args["toRemoveId"]=toRemoveId
    data = EmptyClass()
    data.args = args
    data.method = "unCombineList"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

  def updateEntry(self, entry):
    args = collections.OrderedDict()
    if isinstance(entry,GetShopBaseClass): 
      args["entry"]=json.dumps(entry.__dict__)
    else:
      try:
        args["entry"]=json.dumps(entry)
      except (ValueError, AttributeError):
        args["entry"]=entry
    data = EmptyClass()
    data.args = args
    data.method = "updateEntry"
    data.interfaceName = "core.listmanager.IListManager"
    return self.communicationHelper.sendMessage(data)

class LogoManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def deleteLogo(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "deleteLogo"
    data.interfaceName = "app.logo.ILogoManager"
    return self.communicationHelper.sendMessage(data)

  def getLogo(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getLogo"
    data.interfaceName = "app.logo.ILogoManager"
    return self.communicationHelper.sendMessage(data)

  def setLogo(self, fileId):
    args = collections.OrderedDict()
    if isinstance(fileId,GetShopBaseClass): 
      args["fileId"]=json.dumps(fileId.__dict__)
    else:
      try:
        args["fileId"]=json.dumps(fileId)
      except (ValueError, AttributeError):
        args["fileId"]=fileId
    data = EmptyClass()
    data.args = args
    data.method = "setLogo"
    data.interfaceName = "app.logo.ILogoManager"
    return self.communicationHelper.sendMessage(data)

class MecaApi(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addVehicle(self, phoneNumber, vehicle):
    args = collections.OrderedDict()
    if isinstance(phoneNumber,GetShopBaseClass): 
      args["phoneNumber"]=json.dumps(phoneNumber.__dict__)
    else:
      try:
        args["phoneNumber"]=json.dumps(phoneNumber)
      except (ValueError, AttributeError):
        args["phoneNumber"]=phoneNumber
    if isinstance(vehicle,GetShopBaseClass): 
      args["vehicle"]=json.dumps(vehicle.__dict__)
    else:
      try:
        args["vehicle"]=json.dumps(vehicle)
      except (ValueError, AttributeError):
        args["vehicle"]=vehicle
    data = EmptyClass()
    data.args = args
    data.method = "addVehicle"
    data.interfaceName = "core.mecamanager.IMecaApi"
    return self.communicationHelper.sendMessage(data)

  def changePassword(self, phoneNumber, oldPassword, newPassword1, newPassword2):
    args = collections.OrderedDict()
    if isinstance(phoneNumber,GetShopBaseClass): 
      args["phoneNumber"]=json.dumps(phoneNumber.__dict__)
    else:
      try:
        args["phoneNumber"]=json.dumps(phoneNumber)
      except (ValueError, AttributeError):
        args["phoneNumber"]=phoneNumber
    if isinstance(oldPassword,GetShopBaseClass): 
      args["oldPassword"]=json.dumps(oldPassword.__dict__)
    else:
      try:
        args["oldPassword"]=json.dumps(oldPassword)
      except (ValueError, AttributeError):
        args["oldPassword"]=oldPassword
    if isinstance(newPassword1,GetShopBaseClass): 
      args["newPassword1"]=json.dumps(newPassword1.__dict__)
    else:
      try:
        args["newPassword1"]=json.dumps(newPassword1)
      except (ValueError, AttributeError):
        args["newPassword1"]=newPassword1
    if isinstance(newPassword2,GetShopBaseClass): 
      args["newPassword2"]=json.dumps(newPassword2.__dict__)
    else:
      try:
        args["newPassword2"]=json.dumps(newPassword2)
      except (ValueError, AttributeError):
        args["newPassword2"]=newPassword2
    data = EmptyClass()
    data.args = args
    data.method = "changePassword"
    data.interfaceName = "core.mecamanager.IMecaApi"
    return self.communicationHelper.sendMessage(data)

  def createAccount(self, phoneNumber):
    args = collections.OrderedDict()
    if isinstance(phoneNumber,GetShopBaseClass): 
      args["phoneNumber"]=json.dumps(phoneNumber.__dict__)
    else:
      try:
        args["phoneNumber"]=json.dumps(phoneNumber)
      except (ValueError, AttributeError):
        args["phoneNumber"]=phoneNumber
    data = EmptyClass()
    data.args = args
    data.method = "createAccount"
    data.interfaceName = "core.mecamanager.IMecaApi"
    return self.communicationHelper.sendMessage(data)

  def login(self, phoneNumber, password):
    args = collections.OrderedDict()
    if isinstance(phoneNumber,GetShopBaseClass): 
      args["phoneNumber"]=json.dumps(phoneNumber.__dict__)
    else:
      try:
        args["phoneNumber"]=json.dumps(phoneNumber)
      except (ValueError, AttributeError):
        args["phoneNumber"]=phoneNumber
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "login"
    data.interfaceName = "core.mecamanager.IMecaApi"
    return self.communicationHelper.sendMessage(data)

class MessageManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def collectEmail(self, email):
    args = collections.OrderedDict()
    if isinstance(email,GetShopBaseClass): 
      args["email"]=json.dumps(email.__dict__)
    else:
      try:
        args["email"]=json.dumps(email)
      except (ValueError, AttributeError):
        args["email"]=email
    data = EmptyClass()
    data.args = args
    data.method = "collectEmail"
    data.interfaceName = "core.messagemanager.IMessageManager"
    return self.communicationHelper.sendMessage(data)

  def getSmsCount(self, year, month):
    args = collections.OrderedDict()
    if isinstance(year,GetShopBaseClass): 
      args["year"]=json.dumps(year.__dict__)
    else:
      try:
        args["year"]=json.dumps(year)
      except (ValueError, AttributeError):
        args["year"]=year
    if isinstance(month,GetShopBaseClass): 
      args["month"]=json.dumps(month.__dict__)
    else:
      try:
        args["month"]=json.dumps(month)
      except (ValueError, AttributeError):
        args["month"]=month
    data = EmptyClass()
    data.args = args
    data.method = "getSmsCount"
    data.interfaceName = "core.messagemanager.IMessageManager"
    return self.communicationHelper.sendMessage(data)

  def getSmsLog(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getSmsLog"
    data.interfaceName = "core.messagemanager.IMessageManager"
    return self.communicationHelper.sendMessage(data)

  def sendMail(self, to, toName, subject, content, fromm, fromName):
    args = collections.OrderedDict()
    if isinstance(to,GetShopBaseClass): 
      args["to"]=json.dumps(to.__dict__)
    else:
      try:
        args["to"]=json.dumps(to)
      except (ValueError, AttributeError):
        args["to"]=to
    if isinstance(toName,GetShopBaseClass): 
      args["toName"]=json.dumps(toName.__dict__)
    else:
      try:
        args["toName"]=json.dumps(toName)
      except (ValueError, AttributeError):
        args["toName"]=toName
    if isinstance(subject,GetShopBaseClass): 
      args["subject"]=json.dumps(subject.__dict__)
    else:
      try:
        args["subject"]=json.dumps(subject)
      except (ValueError, AttributeError):
        args["subject"]=subject
    if isinstance(content,GetShopBaseClass): 
      args["content"]=json.dumps(content.__dict__)
    else:
      try:
        args["content"]=json.dumps(content)
      except (ValueError, AttributeError):
        args["content"]=content
    if isinstance(fromm,GetShopBaseClass): 
      args["from"]=json.dumps(fromm.__dict__)
    else:
      try:
        args["from"]=json.dumps(fromm)
      except (ValueError, AttributeError):
        args["from"]=fromm
    if isinstance(fromName,GetShopBaseClass): 
      args["fromName"]=json.dumps(fromName.__dict__)
    else:
      try:
        args["fromName"]=json.dumps(fromName)
      except (ValueError, AttributeError):
        args["fromName"]=fromName
    data = EmptyClass()
    data.args = args
    data.method = "sendMail"
    data.interfaceName = "core.messagemanager.IMessageManager"
    return self.communicationHelper.sendMessage(data)

  def sendMailWithAttachments(self, to, toName, subject, content, fromm, fromName, attachments):
    args = collections.OrderedDict()
    if isinstance(to,GetShopBaseClass): 
      args["to"]=json.dumps(to.__dict__)
    else:
      try:
        args["to"]=json.dumps(to)
      except (ValueError, AttributeError):
        args["to"]=to
    if isinstance(toName,GetShopBaseClass): 
      args["toName"]=json.dumps(toName.__dict__)
    else:
      try:
        args["toName"]=json.dumps(toName)
      except (ValueError, AttributeError):
        args["toName"]=toName
    if isinstance(subject,GetShopBaseClass): 
      args["subject"]=json.dumps(subject.__dict__)
    else:
      try:
        args["subject"]=json.dumps(subject)
      except (ValueError, AttributeError):
        args["subject"]=subject
    if isinstance(content,GetShopBaseClass): 
      args["content"]=json.dumps(content.__dict__)
    else:
      try:
        args["content"]=json.dumps(content)
      except (ValueError, AttributeError):
        args["content"]=content
    if isinstance(fromm,GetShopBaseClass): 
      args["from"]=json.dumps(fromm.__dict__)
    else:
      try:
        args["from"]=json.dumps(fromm)
      except (ValueError, AttributeError):
        args["from"]=fromm
    if isinstance(fromName,GetShopBaseClass): 
      args["fromName"]=json.dumps(fromName.__dict__)
    else:
      try:
        args["fromName"]=json.dumps(fromName)
      except (ValueError, AttributeError):
        args["fromName"]=fromName
    if isinstance(attachments,GetShopBaseClass): 
      args["attachments"]=json.dumps(attachments.__dict__)
    else:
      try:
        args["attachments"]=json.dumps(attachments)
      except (ValueError, AttributeError):
        args["attachments"]=attachments
    data = EmptyClass()
    data.args = args
    data.method = "sendMailWithAttachments"
    data.interfaceName = "core.messagemanager.IMessageManager"
    return self.communicationHelper.sendMessage(data)

class MobileManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def clearBadged(self, tokenId):
    args = collections.OrderedDict()
    if isinstance(tokenId,GetShopBaseClass): 
      args["tokenId"]=json.dumps(tokenId.__dict__)
    else:
      try:
        args["tokenId"]=json.dumps(tokenId)
      except (ValueError, AttributeError):
        args["tokenId"]=tokenId
    data = EmptyClass()
    data.args = args
    data.method = "clearBadged"
    data.interfaceName = "core.mobilemanager.IMobileManager"
    return self.communicationHelper.sendMessage(data)

  def registerToken(self, token):
    args = collections.OrderedDict()
    if isinstance(token,GetShopBaseClass): 
      args["token"]=json.dumps(token.__dict__)
    else:
      try:
        args["token"]=json.dumps(token)
      except (ValueError, AttributeError):
        args["token"]=token
    data = EmptyClass()
    data.args = args
    data.method = "registerToken"
    data.interfaceName = "core.mobilemanager.IMobileManager"
    return self.communicationHelper.sendMessage(data)

  def sendMessageToAll(self, message):
    args = collections.OrderedDict()
    if isinstance(message,GetShopBaseClass): 
      args["message"]=json.dumps(message.__dict__)
    else:
      try:
        args["message"]=json.dumps(message)
      except (ValueError, AttributeError):
        args["message"]=message
    data = EmptyClass()
    data.args = args
    data.method = "sendMessageToAll"
    data.interfaceName = "core.mobilemanager.IMobileManager"
    return self.communicationHelper.sendMessage(data)

  def sendMessageToAllTestUnits(self, message):
    args = collections.OrderedDict()
    if isinstance(message,GetShopBaseClass): 
      args["message"]=json.dumps(message.__dict__)
    else:
      try:
        args["message"]=json.dumps(message)
      except (ValueError, AttributeError):
        args["message"]=message
    data = EmptyClass()
    data.args = args
    data.method = "sendMessageToAllTestUnits"
    data.interfaceName = "core.mobilemanager.IMobileManager"
    return self.communicationHelper.sendMessage(data)

class NewsLetterManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def sendNewsLetter(self, group):
    args = collections.OrderedDict()
    if isinstance(group,GetShopBaseClass): 
      args["group"]=json.dumps(group.__dict__)
    else:
      try:
        args["group"]=json.dumps(group)
      except (ValueError, AttributeError):
        args["group"]=group
    data = EmptyClass()
    data.args = args
    data.method = "sendNewsLetter"
    data.interfaceName = "core.messagemanager.INewsLetterManager"
    return self.communicationHelper.sendMessage(data)

  def sendNewsLetterPreview(self, group):
    args = collections.OrderedDict()
    if isinstance(group,GetShopBaseClass): 
      args["group"]=json.dumps(group.__dict__)
    else:
      try:
        args["group"]=json.dumps(group)
      except (ValueError, AttributeError):
        args["group"]=group
    data = EmptyClass()
    data.args = args
    data.method = "sendNewsLetterPreview"
    data.interfaceName = "core.messagemanager.INewsLetterManager"
    return self.communicationHelper.sendMessage(data)

class NewsManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addNews(self, newsEntry):
    args = collections.OrderedDict()
    if isinstance(newsEntry,GetShopBaseClass): 
      args["newsEntry"]=json.dumps(newsEntry.__dict__)
    else:
      try:
        args["newsEntry"]=json.dumps(newsEntry)
      except (ValueError, AttributeError):
        args["newsEntry"]=newsEntry
    data = EmptyClass()
    data.args = args
    data.method = "addNews"
    data.interfaceName = "app.news.INewsManager"
    return self.communicationHelper.sendMessage(data)

  def addSubscriber(self, email):
    args = collections.OrderedDict()
    if isinstance(email,GetShopBaseClass): 
      args["email"]=json.dumps(email.__dict__)
    else:
      try:
        args["email"]=json.dumps(email)
      except (ValueError, AttributeError):
        args["email"]=email
    data = EmptyClass()
    data.args = args
    data.method = "addSubscriber"
    data.interfaceName = "app.news.INewsManager"
    return self.communicationHelper.sendMessage(data)

  def deleteNews(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "deleteNews"
    data.interfaceName = "app.news.INewsManager"
    return self.communicationHelper.sendMessage(data)

  def getAllNews(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllNews"
    data.interfaceName = "app.news.INewsManager"
    return self.communicationHelper.sendMessage(data)

  def getAllSubscribers(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllSubscribers"
    data.interfaceName = "app.news.INewsManager"
    return self.communicationHelper.sendMessage(data)

  def publishNews(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "publishNews"
    data.interfaceName = "app.news.INewsManager"
    return self.communicationHelper.sendMessage(data)

  def removeSubscriber(self, subscriberId):
    args = collections.OrderedDict()
    if isinstance(subscriberId,GetShopBaseClass): 
      args["subscriberId"]=json.dumps(subscriberId.__dict__)
    else:
      try:
        args["subscriberId"]=json.dumps(subscriberId)
      except (ValueError, AttributeError):
        args["subscriberId"]=subscriberId
    data = EmptyClass()
    data.args = args
    data.method = "removeSubscriber"
    data.interfaceName = "app.news.INewsManager"
    return self.communicationHelper.sendMessage(data)

class OrderManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def changeOrderStatus(self, id, status):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    if isinstance(status,GetShopBaseClass): 
      args["status"]=json.dumps(status.__dict__)
    else:
      try:
        args["status"]=json.dumps(status)
      except (ValueError, AttributeError):
        args["status"]=status
    data = EmptyClass()
    data.args = args
    data.method = "changeOrderStatus"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def changeOrderType(self, orderId, paymentTypeId):
    args = collections.OrderedDict()
    if isinstance(orderId,GetShopBaseClass): 
      args["orderId"]=json.dumps(orderId.__dict__)
    else:
      try:
        args["orderId"]=json.dumps(orderId)
      except (ValueError, AttributeError):
        args["orderId"]=orderId
    if isinstance(paymentTypeId,GetShopBaseClass): 
      args["paymentTypeId"]=json.dumps(paymentTypeId.__dict__)
    else:
      try:
        args["paymentTypeId"]=json.dumps(paymentTypeId)
      except (ValueError, AttributeError):
        args["paymentTypeId"]=paymentTypeId
    data = EmptyClass()
    data.args = args
    data.method = "changeOrderType"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def createOrder(self, address):
    args = collections.OrderedDict()
    if isinstance(address,GetShopBaseClass): 
      args["address"]=json.dumps(address.__dict__)
    else:
      try:
        args["address"]=json.dumps(address)
      except (ValueError, AttributeError):
        args["address"]=address
    data = EmptyClass()
    data.args = args
    data.method = "createOrder"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def createOrderByCustomerReference(self, referenceKey):
    args = collections.OrderedDict()
    if isinstance(referenceKey,GetShopBaseClass): 
      args["referenceKey"]=json.dumps(referenceKey.__dict__)
    else:
      try:
        args["referenceKey"]=json.dumps(referenceKey)
      except (ValueError, AttributeError):
        args["referenceKey"]=referenceKey
    data = EmptyClass()
    data.args = args
    data.method = "createOrderByCustomerReference"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def createOrderForUser(self, userId):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    data = EmptyClass()
    data.args = args
    data.method = "createOrderForUser"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getAllOrdersForUser(self, userId):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    data = EmptyClass()
    data.args = args
    data.method = "getAllOrdersForUser"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getAllOrdersOnProduct(self, productId):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    data = EmptyClass()
    data.args = args
    data.method = "getAllOrdersOnProduct"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getMostSoldProducts(self, numberOfProducts):
    args = collections.OrderedDict()
    if isinstance(numberOfProducts,GetShopBaseClass): 
      args["numberOfProducts"]=json.dumps(numberOfProducts.__dict__)
    else:
      try:
        args["numberOfProducts"]=json.dumps(numberOfProducts)
      except (ValueError, AttributeError):
        args["numberOfProducts"]=numberOfProducts
    data = EmptyClass()
    data.args = args
    data.method = "getMostSoldProducts"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getOrder(self, orderId):
    args = collections.OrderedDict()
    if isinstance(orderId,GetShopBaseClass): 
      args["orderId"]=json.dumps(orderId.__dict__)
    else:
      try:
        args["orderId"]=json.dumps(orderId)
      except (ValueError, AttributeError):
        args["orderId"]=orderId
    data = EmptyClass()
    data.args = args
    data.method = "getOrder"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getOrderByReference(self, referenceId):
    args = collections.OrderedDict()
    if isinstance(referenceId,GetShopBaseClass): 
      args["referenceId"]=json.dumps(referenceId.__dict__)
    else:
      try:
        args["referenceId"]=json.dumps(referenceId)
      except (ValueError, AttributeError):
        args["referenceId"]=referenceId
    data = EmptyClass()
    data.args = args
    data.method = "getOrderByReference"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getOrderByincrementOrderId(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getOrderByincrementOrderId"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getOrderSecure(self, orderId):
    args = collections.OrderedDict()
    if isinstance(orderId,GetShopBaseClass): 
      args["orderId"]=json.dumps(orderId.__dict__)
    else:
      try:
        args["orderId"]=json.dumps(orderId)
      except (ValueError, AttributeError):
        args["orderId"]=orderId
    data = EmptyClass()
    data.args = args
    data.method = "getOrderSecure"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getOrders(self, orderIds, page, pageSize):
    args = collections.OrderedDict()
    if isinstance(orderIds,GetShopBaseClass): 
      args["orderIds"]=json.dumps(orderIds.__dict__)
    else:
      try:
        args["orderIds"]=json.dumps(orderIds)
      except (ValueError, AttributeError):
        args["orderIds"]=orderIds
    if isinstance(page,GetShopBaseClass): 
      args["page"]=json.dumps(page.__dict__)
    else:
      try:
        args["page"]=json.dumps(page)
      except (ValueError, AttributeError):
        args["page"]=page
    if isinstance(pageSize,GetShopBaseClass): 
      args["pageSize"]=json.dumps(pageSize.__dict__)
    else:
      try:
        args["pageSize"]=json.dumps(pageSize)
      except (ValueError, AttributeError):
        args["pageSize"]=pageSize
    data = EmptyClass()
    data.args = args
    data.method = "getOrders"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getOrdersFromPeriode(self, start, end, statistics):
    args = collections.OrderedDict()
    if isinstance(start,GetShopBaseClass): 
      args["start"]=json.dumps(start.__dict__)
    else:
      try:
        args["start"]=json.dumps(start)
      except (ValueError, AttributeError):
        args["start"]=start
    if isinstance(end,GetShopBaseClass): 
      args["end"]=json.dumps(end.__dict__)
    else:
      try:
        args["end"]=json.dumps(end)
      except (ValueError, AttributeError):
        args["end"]=end
    if isinstance(statistics,GetShopBaseClass): 
      args["statistics"]=json.dumps(statistics.__dict__)
    else:
      try:
        args["statistics"]=json.dumps(statistics)
      except (ValueError, AttributeError):
        args["statistics"]=statistics
    data = EmptyClass()
    data.args = args
    data.method = "getOrdersFromPeriode"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getOrdersNotTransferredToAccountingSystem(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getOrdersNotTransferredToAccountingSystem"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getOrdersToCapture(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getOrdersToCapture"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getPageCount(self, pageSize, searchWord):
    args = collections.OrderedDict()
    if isinstance(pageSize,GetShopBaseClass): 
      args["pageSize"]=json.dumps(pageSize.__dict__)
    else:
      try:
        args["pageSize"]=json.dumps(pageSize)
      except (ValueError, AttributeError):
        args["pageSize"]=pageSize
    if isinstance(searchWord,GetShopBaseClass): 
      args["searchWord"]=json.dumps(searchWord.__dict__)
    else:
      try:
        args["searchWord"]=json.dumps(searchWord)
      except (ValueError, AttributeError):
        args["searchWord"]=searchWord
    data = EmptyClass()
    data.args = args
    data.method = "getPageCount"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getSalesNumber(self, year):
    args = collections.OrderedDict()
    if isinstance(year,GetShopBaseClass): 
      args["year"]=json.dumps(year.__dict__)
    else:
      try:
        args["year"]=json.dumps(year)
      except (ValueError, AttributeError):
        args["year"]=year
    data = EmptyClass()
    data.args = args
    data.method = "getSalesNumber"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getSalesStatistics(self, startDate, endDate, type):
    args = collections.OrderedDict()
    if isinstance(startDate,GetShopBaseClass): 
      args["startDate"]=json.dumps(startDate.__dict__)
    else:
      try:
        args["startDate"]=json.dumps(startDate)
      except (ValueError, AttributeError):
        args["startDate"]=startDate
    if isinstance(endDate,GetShopBaseClass): 
      args["endDate"]=json.dumps(endDate.__dict__)
    else:
      try:
        args["endDate"]=json.dumps(endDate)
      except (ValueError, AttributeError):
        args["endDate"]=endDate
    if isinstance(type,GetShopBaseClass): 
      args["type"]=json.dumps(type.__dict__)
    else:
      try:
        args["type"]=json.dumps(type)
      except (ValueError, AttributeError):
        args["type"]=type
    data = EmptyClass()
    data.args = args
    data.method = "getSalesStatistics"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getTaxes(self, order):
    args = collections.OrderedDict()
    if isinstance(order,GetShopBaseClass): 
      args["order"]=json.dumps(order.__dict__)
    else:
      try:
        args["order"]=json.dumps(order)
      except (ValueError, AttributeError):
        args["order"]=order
    data = EmptyClass()
    data.args = args
    data.method = "getTaxes"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getTotalAmount(self, order):
    args = collections.OrderedDict()
    if isinstance(order,GetShopBaseClass): 
      args["order"]=json.dumps(order.__dict__)
    else:
      try:
        args["order"]=json.dumps(order)
      except (ValueError, AttributeError):
        args["order"]=order
    data = EmptyClass()
    data.args = args
    data.method = "getTotalAmount"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def getTotalSalesAmount(self, year, month, week, day, type):
    args = collections.OrderedDict()
    if isinstance(year,GetShopBaseClass): 
      args["year"]=json.dumps(year.__dict__)
    else:
      try:
        args["year"]=json.dumps(year)
      except (ValueError, AttributeError):
        args["year"]=year
    if isinstance(month,GetShopBaseClass): 
      args["month"]=json.dumps(month.__dict__)
    else:
      try:
        args["month"]=json.dumps(month)
      except (ValueError, AttributeError):
        args["month"]=month
    if isinstance(week,GetShopBaseClass): 
      args["week"]=json.dumps(week.__dict__)
    else:
      try:
        args["week"]=json.dumps(week)
      except (ValueError, AttributeError):
        args["week"]=week
    if isinstance(day,GetShopBaseClass): 
      args["day"]=json.dumps(day.__dict__)
    else:
      try:
        args["day"]=json.dumps(day)
      except (ValueError, AttributeError):
        args["day"]=day
    if isinstance(type,GetShopBaseClass): 
      args["type"]=json.dumps(type.__dict__)
    else:
      try:
        args["type"]=json.dumps(type)
      except (ValueError, AttributeError):
        args["type"]=type
    data = EmptyClass()
    data.args = args
    data.method = "getTotalSalesAmount"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def logTransactionEntry(self, orderId, entry):
    args = collections.OrderedDict()
    if isinstance(orderId,GetShopBaseClass): 
      args["orderId"]=json.dumps(orderId.__dict__)
    else:
      try:
        args["orderId"]=json.dumps(orderId)
      except (ValueError, AttributeError):
        args["orderId"]=orderId
    if isinstance(entry,GetShopBaseClass): 
      args["entry"]=json.dumps(entry.__dict__)
    else:
      try:
        args["entry"]=json.dumps(entry)
      except (ValueError, AttributeError):
        args["entry"]=entry
    data = EmptyClass()
    data.args = args
    data.method = "logTransactionEntry"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def saveOrder(self, order):
    args = collections.OrderedDict()
    if isinstance(order,GetShopBaseClass): 
      args["order"]=json.dumps(order.__dict__)
    else:
      try:
        args["order"]=json.dumps(order)
      except (ValueError, AttributeError):
        args["order"]=order
    data = EmptyClass()
    data.args = args
    data.method = "saveOrder"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def searchForOrders(self, searchWord, page, pageSize):
    args = collections.OrderedDict()
    if isinstance(searchWord,GetShopBaseClass): 
      args["searchWord"]=json.dumps(searchWord.__dict__)
    else:
      try:
        args["searchWord"]=json.dumps(searchWord)
      except (ValueError, AttributeError):
        args["searchWord"]=searchWord
    if isinstance(page,GetShopBaseClass): 
      args["page"]=json.dumps(page.__dict__)
    else:
      try:
        args["page"]=json.dumps(page)
      except (ValueError, AttributeError):
        args["page"]=page
    if isinstance(pageSize,GetShopBaseClass): 
      args["pageSize"]=json.dumps(pageSize.__dict__)
    else:
      try:
        args["pageSize"]=json.dumps(pageSize)
      except (ValueError, AttributeError):
        args["pageSize"]=pageSize
    data = EmptyClass()
    data.args = args
    data.method = "searchForOrders"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def setOrderStatus(self, password, orderId, currency, price, status):
    args = collections.OrderedDict()
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    if isinstance(orderId,GetShopBaseClass): 
      args["orderId"]=json.dumps(orderId.__dict__)
    else:
      try:
        args["orderId"]=json.dumps(orderId)
      except (ValueError, AttributeError):
        args["orderId"]=orderId
    if isinstance(currency,GetShopBaseClass): 
      args["currency"]=json.dumps(currency.__dict__)
    else:
      try:
        args["currency"]=json.dumps(currency)
      except (ValueError, AttributeError):
        args["currency"]=currency
    if isinstance(price,GetShopBaseClass): 
      args["price"]=json.dumps(price.__dict__)
    else:
      try:
        args["price"]=json.dumps(price)
      except (ValueError, AttributeError):
        args["price"]=price
    if isinstance(status,GetShopBaseClass): 
      args["status"]=json.dumps(status.__dict__)
    else:
      try:
        args["status"]=json.dumps(status)
      except (ValueError, AttributeError):
        args["status"]=status
    data = EmptyClass()
    data.args = args
    data.method = "setOrderStatus"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

  def updatePriceForOrderLine(self, cartItemId, orderId, price):
    args = collections.OrderedDict()
    if isinstance(cartItemId,GetShopBaseClass): 
      args["cartItemId"]=json.dumps(cartItemId.__dict__)
    else:
      try:
        args["cartItemId"]=json.dumps(cartItemId)
      except (ValueError, AttributeError):
        args["cartItemId"]=cartItemId
    if isinstance(orderId,GetShopBaseClass): 
      args["orderId"]=json.dumps(orderId.__dict__)
    else:
      try:
        args["orderId"]=json.dumps(orderId)
      except (ValueError, AttributeError):
        args["orderId"]=orderId
    if isinstance(price,GetShopBaseClass): 
      args["price"]=json.dumps(price.__dict__)
    else:
      try:
        args["price"]=json.dumps(price)
      except (ValueError, AttributeError):
        args["price"]=price
    data = EmptyClass()
    data.args = args
    data.method = "updatePriceForOrderLine"
    data.interfaceName = "core.ordermanager.IOrderManager"
    return self.communicationHelper.sendMessage(data)

class PageManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addApplication(self, applicationId, pageCellId):
    args = collections.OrderedDict()
    if isinstance(applicationId,GetShopBaseClass): 
      args["applicationId"]=json.dumps(applicationId.__dict__)
    else:
      try:
        args["applicationId"]=json.dumps(applicationId)
      except (ValueError, AttributeError):
        args["applicationId"]=applicationId
    if isinstance(pageCellId,GetShopBaseClass): 
      args["pageCellId"]=json.dumps(pageCellId.__dict__)
    else:
      try:
        args["pageCellId"]=json.dumps(pageCellId)
      except (ValueError, AttributeError):
        args["pageCellId"]=pageCellId
    data = EmptyClass()
    data.args = args
    data.method = "addApplication"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def addExistingApplicationToPageArea(self, pageId, appId, area):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(appId,GetShopBaseClass): 
      args["appId"]=json.dumps(appId.__dict__)
    else:
      try:
        args["appId"]=json.dumps(appId)
      except (ValueError, AttributeError):
        args["appId"]=appId
    if isinstance(area,GetShopBaseClass): 
      args["area"]=json.dumps(area.__dict__)
    else:
      try:
        args["area"]=json.dumps(area)
      except (ValueError, AttributeError):
        args["area"]=area
    data = EmptyClass()
    data.args = args
    data.method = "addExistingApplicationToPageArea"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def addLayoutCell(self, pageId, incell, beforecell, direction, area):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(incell,GetShopBaseClass): 
      args["incell"]=json.dumps(incell.__dict__)
    else:
      try:
        args["incell"]=json.dumps(incell)
      except (ValueError, AttributeError):
        args["incell"]=incell
    if isinstance(beforecell,GetShopBaseClass): 
      args["beforecell"]=json.dumps(beforecell.__dict__)
    else:
      try:
        args["beforecell"]=json.dumps(beforecell)
      except (ValueError, AttributeError):
        args["beforecell"]=beforecell
    if isinstance(direction,GetShopBaseClass): 
      args["direction"]=json.dumps(direction.__dict__)
    else:
      try:
        args["direction"]=json.dumps(direction)
      except (ValueError, AttributeError):
        args["direction"]=direction
    if isinstance(area,GetShopBaseClass): 
      args["area"]=json.dumps(area.__dict__)
    else:
      try:
        args["area"]=json.dumps(area)
      except (ValueError, AttributeError):
        args["area"]=area
    data = EmptyClass()
    data.args = args
    data.method = "addLayoutCell"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def changePageUserLevel(self, pageId, userLevel):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(userLevel,GetShopBaseClass): 
      args["userLevel"]=json.dumps(userLevel.__dict__)
    else:
      try:
        args["userLevel"]=json.dumps(userLevel)
      except (ValueError, AttributeError):
        args["userLevel"]=userLevel
    data = EmptyClass()
    data.args = args
    data.method = "changePageUserLevel"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def clearPage(self, pageId):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    data = EmptyClass()
    data.args = args
    data.method = "clearPage"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def clearPageArea(self, pageId, pageArea):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(pageArea,GetShopBaseClass): 
      args["pageArea"]=json.dumps(pageArea.__dict__)
    else:
      try:
        args["pageArea"]=json.dumps(pageArea)
      except (ValueError, AttributeError):
        args["pageArea"]=pageArea
    data = EmptyClass()
    data.args = args
    data.method = "clearPageArea"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def createHeaderFooter(self, type):
    args = collections.OrderedDict()
    if isinstance(type,GetShopBaseClass): 
      args["type"]=json.dumps(type.__dict__)
    else:
      try:
        args["type"]=json.dumps(type)
      except (ValueError, AttributeError):
        args["type"]=type
    data = EmptyClass()
    data.args = args
    data.method = "createHeaderFooter"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def createNewRow(self, pageId):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    data = EmptyClass()
    data.args = args
    data.method = "createNewRow"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def createPage(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "createPage"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def deleteApplication(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "deleteApplication"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def deletePage(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "deletePage"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def dropCell(self, pageId, cellId):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    data = EmptyClass()
    data.args = args
    data.method = "dropCell"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def getApplications(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getApplications"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def getApplicationsBasedOnApplicationSettingsId(self, appSettingsId):
    args = collections.OrderedDict()
    if isinstance(appSettingsId,GetShopBaseClass): 
      args["appSettingsId"]=json.dumps(appSettingsId.__dict__)
    else:
      try:
        args["appSettingsId"]=json.dumps(appSettingsId)
      except (ValueError, AttributeError):
        args["appSettingsId"]=appSettingsId
    data = EmptyClass()
    data.args = args
    data.method = "getApplicationsBasedOnApplicationSettingsId"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def getApplicationsByPageAreaAndSettingsId(self, appSettingsId, pageArea):
    args = collections.OrderedDict()
    if isinstance(appSettingsId,GetShopBaseClass): 
      args["appSettingsId"]=json.dumps(appSettingsId.__dict__)
    else:
      try:
        args["appSettingsId"]=json.dumps(appSettingsId)
      except (ValueError, AttributeError):
        args["appSettingsId"]=appSettingsId
    if isinstance(pageArea,GetShopBaseClass): 
      args["pageArea"]=json.dumps(pageArea.__dict__)
    else:
      try:
        args["pageArea"]=json.dumps(pageArea)
      except (ValueError, AttributeError):
        args["pageArea"]=pageArea
    data = EmptyClass()
    data.args = args
    data.method = "getApplicationsByPageAreaAndSettingsId"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def getApplicationsByType(self, type):
    args = collections.OrderedDict()
    if isinstance(type,GetShopBaseClass): 
      args["type"]=json.dumps(type.__dict__)
    else:
      try:
        args["type"]=json.dumps(type)
      except (ValueError, AttributeError):
        args["type"]=type
    data = EmptyClass()
    data.args = args
    data.method = "getApplicationsByType"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def getApplicationsForPage(self, pageId):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    data = EmptyClass()
    data.args = args
    data.method = "getApplicationsForPage"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def getCell(self, pageId, cellId):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    data = EmptyClass()
    data.args = args
    data.method = "getCell"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def getPage(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getPage"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def getPagesForApplications(self, appIds):
    args = collections.OrderedDict()
    if isinstance(appIds,GetShopBaseClass): 
      args["appIds"]=json.dumps(appIds.__dict__)
    else:
      try:
        args["appIds"]=json.dumps(appIds)
      except (ValueError, AttributeError):
        args["appIds"]=appIds
    data = EmptyClass()
    data.args = args
    data.method = "getPagesForApplications"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def getSecuredSettings(self, applicationInstanceId):
    args = collections.OrderedDict()
    if isinstance(applicationInstanceId,GetShopBaseClass): 
      args["applicationInstanceId"]=json.dumps(applicationInstanceId.__dict__)
    else:
      try:
        args["applicationInstanceId"]=json.dumps(applicationInstanceId)
      except (ValueError, AttributeError):
        args["applicationInstanceId"]=applicationInstanceId
    data = EmptyClass()
    data.args = args
    data.method = "getSecuredSettings"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def getSecuredSettingsInternal(self, appName):
    args = collections.OrderedDict()
    if isinstance(appName,GetShopBaseClass): 
      args["appName"]=json.dumps(appName.__dict__)
    else:
      try:
        args["appName"]=json.dumps(appName)
      except (ValueError, AttributeError):
        args["appName"]=appName
    data = EmptyClass()
    data.args = args
    data.method = "getSecuredSettingsInternal"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def linkPageCell(self, pageId, cellId, link):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    if isinstance(link,GetShopBaseClass): 
      args["link"]=json.dumps(link.__dict__)
    else:
      try:
        args["link"]=json.dumps(link)
      except (ValueError, AttributeError):
        args["link"]=link
    data = EmptyClass()
    data.args = args
    data.method = "linkPageCell"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def moveCell(self, pageId, cellId, up):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    if isinstance(up,GetShopBaseClass): 
      args["up"]=json.dumps(up.__dict__)
    else:
      try:
        args["up"]=json.dumps(up)
      except (ValueError, AttributeError):
        args["up"]=up
    data = EmptyClass()
    data.args = args
    data.method = "moveCell"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def moveCellMobile(self, pageId, cellId, moveUp):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    if isinstance(moveUp,GetShopBaseClass): 
      args["moveUp"]=json.dumps(moveUp.__dict__)
    else:
      try:
        args["moveUp"]=json.dumps(moveUp)
      except (ValueError, AttributeError):
        args["moveUp"]=moveUp
    data = EmptyClass()
    data.args = args
    data.method = "moveCellMobile"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def removeAppFromCell(self, pageId, cellid):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellid,GetShopBaseClass): 
      args["cellid"]=json.dumps(cellid.__dict__)
    else:
      try:
        args["cellid"]=json.dumps(cellid)
      except (ValueError, AttributeError):
        args["cellid"]=cellid
    data = EmptyClass()
    data.args = args
    data.method = "removeAppFromCell"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def saveApplicationConfiguration(self, config):
    args = collections.OrderedDict()
    if isinstance(config,GetShopBaseClass): 
      args["config"]=json.dumps(config.__dict__)
    else:
      try:
        args["config"]=json.dumps(config)
      except (ValueError, AttributeError):
        args["config"]=config
    data = EmptyClass()
    data.args = args
    data.method = "saveApplicationConfiguration"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def saveCell(self, pageId, cell):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cell,GetShopBaseClass): 
      args["cell"]=json.dumps(cell.__dict__)
    else:
      try:
        args["cell"]=json.dumps(cell)
      except (ValueError, AttributeError):
        args["cell"]=cell
    data = EmptyClass()
    data.args = args
    data.method = "saveCell"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def saveCellPosition(self, pageId, cellId, data):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    if isinstance(data,GetShopBaseClass): 
      args["data"]=json.dumps(data.__dict__)
    else:
      try:
        args["data"]=json.dumps(data)
      except (ValueError, AttributeError):
        args["data"]=data
    data = EmptyClass()
    data.args = args
    data.method = "saveCellPosition"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def savePage(self, page):
    args = collections.OrderedDict()
    if isinstance(page,GetShopBaseClass): 
      args["page"]=json.dumps(page.__dict__)
    else:
      try:
        args["page"]=json.dumps(page)
      except (ValueError, AttributeError):
        args["page"]=page
    data = EmptyClass()
    data.args = args
    data.method = "savePage"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def savePageCellSettings(self, pageId, cellId, settings):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    if isinstance(settings,GetShopBaseClass): 
      args["settings"]=json.dumps(settings.__dict__)
    else:
      try:
        args["settings"]=json.dumps(settings)
      except (ValueError, AttributeError):
        args["settings"]=settings
    data = EmptyClass()
    data.args = args
    data.method = "savePageCellSettings"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def setCarouselConfig(self, pageId, cellId, config):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    if isinstance(config,GetShopBaseClass): 
      args["config"]=json.dumps(config.__dict__)
    else:
      try:
        args["config"]=json.dumps(config)
      except (ValueError, AttributeError):
        args["config"]=config
    data = EmptyClass()
    data.args = args
    data.method = "setCarouselConfig"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def setCellMode(self, pageId, cellId, mode):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    if isinstance(mode,GetShopBaseClass): 
      args["mode"]=json.dumps(mode.__dict__)
    else:
      try:
        args["mode"]=json.dumps(mode)
      except (ValueError, AttributeError):
        args["mode"]=mode
    data = EmptyClass()
    data.args = args
    data.method = "setCellMode"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def setCellName(self, pageId, cellId, cellName):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    if isinstance(cellName,GetShopBaseClass): 
      args["cellName"]=json.dumps(cellName.__dict__)
    else:
      try:
        args["cellName"]=json.dumps(cellName)
      except (ValueError, AttributeError):
        args["cellName"]=cellName
    data = EmptyClass()
    data.args = args
    data.method = "setCellName"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def setPageDescription(self, pageId, description):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(description,GetShopBaseClass): 
      args["description"]=json.dumps(description.__dict__)
    else:
      try:
        args["description"]=json.dumps(description)
      except (ValueError, AttributeError):
        args["description"]=description
    data = EmptyClass()
    data.args = args
    data.method = "setPageDescription"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def setParentPage(self, pageId, parentPageId):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(parentPageId,GetShopBaseClass): 
      args["parentPageId"]=json.dumps(parentPageId.__dict__)
    else:
      try:
        args["parentPageId"]=json.dumps(parentPageId)
      except (ValueError, AttributeError):
        args["parentPageId"]=parentPageId
    data = EmptyClass()
    data.args = args
    data.method = "setParentPage"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def setStylesOnCell(self, pageId, cellId, styles, innerStyles, width):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    if isinstance(styles,GetShopBaseClass): 
      args["styles"]=json.dumps(styles.__dict__)
    else:
      try:
        args["styles"]=json.dumps(styles)
      except (ValueError, AttributeError):
        args["styles"]=styles
    if isinstance(innerStyles,GetShopBaseClass): 
      args["innerStyles"]=json.dumps(innerStyles.__dict__)
    else:
      try:
        args["innerStyles"]=json.dumps(innerStyles)
      except (ValueError, AttributeError):
        args["innerStyles"]=innerStyles
    if isinstance(width,GetShopBaseClass): 
      args["width"]=json.dumps(width.__dict__)
    else:
      try:
        args["width"]=json.dumps(width)
      except (ValueError, AttributeError):
        args["width"]=width
    data = EmptyClass()
    data.args = args
    data.method = "setStylesOnCell"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def setWidth(self, pageId, cellId, outerWidth, outerWidthWithMargins):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    if isinstance(outerWidth,GetShopBaseClass): 
      args["outerWidth"]=json.dumps(outerWidth.__dict__)
    else:
      try:
        args["outerWidth"]=json.dumps(outerWidth)
      except (ValueError, AttributeError):
        args["outerWidth"]=outerWidth
    if isinstance(outerWidthWithMargins,GetShopBaseClass): 
      args["outerWidthWithMargins"]=json.dumps(outerWidthWithMargins.__dict__)
    else:
      try:
        args["outerWidthWithMargins"]=json.dumps(outerWidthWithMargins)
      except (ValueError, AttributeError):
        args["outerWidthWithMargins"]=outerWidthWithMargins
    data = EmptyClass()
    data.args = args
    data.method = "setWidth"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def swapAppWithCell(self, pageId, fromCellId, toCellId):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(fromCellId,GetShopBaseClass): 
      args["fromCellId"]=json.dumps(fromCellId.__dict__)
    else:
      try:
        args["fromCellId"]=json.dumps(fromCellId)
      except (ValueError, AttributeError):
        args["fromCellId"]=fromCellId
    if isinstance(toCellId,GetShopBaseClass): 
      args["toCellId"]=json.dumps(toCellId.__dict__)
    else:
      try:
        args["toCellId"]=json.dumps(toCellId)
      except (ValueError, AttributeError):
        args["toCellId"]=toCellId
    data = EmptyClass()
    data.args = args
    data.method = "swapAppWithCell"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def toggleHiddenOnMobile(self, pageId, cellId, hide):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    if isinstance(hide,GetShopBaseClass): 
      args["hide"]=json.dumps(hide.__dict__)
    else:
      try:
        args["hide"]=json.dumps(hide)
      except (ValueError, AttributeError):
        args["hide"]=hide
    data = EmptyClass()
    data.args = args
    data.method = "toggleHiddenOnMobile"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def togglePinArea(self, pageId, cellId):
    args = collections.OrderedDict()
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    data = EmptyClass()
    data.args = args
    data.method = "togglePinArea"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def translatePages(self, pages):
    args = collections.OrderedDict()
    if isinstance(pages,GetShopBaseClass): 
      args["pages"]=json.dumps(pages.__dict__)
    else:
      try:
        args["pages"]=json.dumps(pages)
      except (ValueError, AttributeError):
        args["pages"]=pages
    data = EmptyClass()
    data.args = args
    data.method = "translatePages"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

  def updateCellLayout(self, layout, pageId, cellId):
    args = collections.OrderedDict()
    if isinstance(layout,GetShopBaseClass): 
      args["layout"]=json.dumps(layout.__dict__)
    else:
      try:
        args["layout"]=json.dumps(layout)
      except (ValueError, AttributeError):
        args["layout"]=layout
    if isinstance(pageId,GetShopBaseClass): 
      args["pageId"]=json.dumps(pageId.__dict__)
    else:
      try:
        args["pageId"]=json.dumps(pageId)
      except (ValueError, AttributeError):
        args["pageId"]=pageId
    if isinstance(cellId,GetShopBaseClass): 
      args["cellId"]=json.dumps(cellId.__dict__)
    else:
      try:
        args["cellId"]=json.dumps(cellId)
      except (ValueError, AttributeError):
        args["cellId"]=cellId
    data = EmptyClass()
    data.args = args
    data.method = "updateCellLayout"
    data.interfaceName = "core.pagemanager.IPageManager"
    return self.communicationHelper.sendMessage(data)

class PkkControlManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def getPkkControlData(self, licensePlate):
    args = collections.OrderedDict()
    if isinstance(licensePlate,GetShopBaseClass): 
      args["licensePlate"]=json.dumps(licensePlate.__dict__)
    else:
      try:
        args["licensePlate"]=json.dumps(licensePlate)
      except (ValueError, AttributeError):
        args["licensePlate"]=licensePlate
    data = EmptyClass()
    data.args = args
    data.method = "getPkkControlData"
    data.interfaceName = "core.pkk.IPkkControlManager"
    return self.communicationHelper.sendMessage(data)

  def getPkkControls(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getPkkControls"
    data.interfaceName = "core.pkk.IPkkControlManager"
    return self.communicationHelper.sendMessage(data)

  def registerPkkControl(self, data):
    args = collections.OrderedDict()
    if isinstance(data,GetShopBaseClass): 
      args["data"]=json.dumps(data.__dict__)
    else:
      try:
        args["data"]=json.dumps(data)
      except (ValueError, AttributeError):
        args["data"]=data
    data = EmptyClass()
    data.args = args
    data.method = "registerPkkControl"
    data.interfaceName = "core.pkk.IPkkControlManager"
    return self.communicationHelper.sendMessage(data)

  def removePkkControl(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "removePkkControl"
    data.interfaceName = "core.pkk.IPkkControlManager"
    return self.communicationHelper.sendMessage(data)

class ProductManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def changeStockQuantity(self, productId, count):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(count,GetShopBaseClass): 
      args["count"]=json.dumps(count.__dict__)
    else:
      try:
        args["count"]=json.dumps(count)
      except (ValueError, AttributeError):
        args["count"]=count
    data = EmptyClass()
    data.args = args
    data.method = "changeStockQuantity"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def createProduct(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "createProduct"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def createProductList(self, listName):
    args = collections.OrderedDict()
    if isinstance(listName,GetShopBaseClass): 
      args["listName"]=json.dumps(listName.__dict__)
    else:
      try:
        args["listName"]=json.dumps(listName)
      except (ValueError, AttributeError):
        args["listName"]=listName
    data = EmptyClass()
    data.args = args
    data.method = "createProductList"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def deleteProductList(self, listId):
    args = collections.OrderedDict()
    if isinstance(listId,GetShopBaseClass): 
      args["listId"]=json.dumps(listId.__dict__)
    else:
      try:
        args["listId"]=json.dumps(listId)
      except (ValueError, AttributeError):
        args["listId"]=listId
    data = EmptyClass()
    data.args = args
    data.method = "deleteProductList"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getAllAttributes(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllAttributes"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getAllProducts(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllProducts"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getAllProductsLight(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllProductsLight"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getAttributeSummary(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAttributeSummary"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getLatestProducts(self, count):
    args = collections.OrderedDict()
    if isinstance(count,GetShopBaseClass): 
      args["count"]=json.dumps(count.__dict__)
    else:
      try:
        args["count"]=json.dumps(count)
      except (ValueError, AttributeError):
        args["count"]=count
    data = EmptyClass()
    data.args = args
    data.method = "getLatestProducts"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getPageIdByName(self, productName):
    args = collections.OrderedDict()
    if isinstance(productName,GetShopBaseClass): 
      args["productName"]=json.dumps(productName.__dict__)
    else:
      try:
        args["productName"]=json.dumps(productName)
      except (ValueError, AttributeError):
        args["productName"]=productName
    data = EmptyClass()
    data.args = args
    data.method = "getPageIdByName"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getPrice(self, productId, variations):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(variations,GetShopBaseClass): 
      args["variations"]=json.dumps(variations.__dict__)
    else:
      try:
        args["variations"]=json.dumps(variations)
      except (ValueError, AttributeError):
        args["variations"]=variations
    data = EmptyClass()
    data.args = args
    data.method = "getPrice"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getProduct(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getProduct"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getProductByPage(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getProductByPage"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getProductFromApplicationId(self, app_uuid):
    args = collections.OrderedDict()
    if isinstance(app_uuid,GetShopBaseClass): 
      args["app_uuid"]=json.dumps(app_uuid.__dict__)
    else:
      try:
        args["app_uuid"]=json.dumps(app_uuid)
      except (ValueError, AttributeError):
        args["app_uuid"]=app_uuid
    data = EmptyClass()
    data.args = args
    data.method = "getProductFromApplicationId"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getProductList(self, listId):
    args = collections.OrderedDict()
    if isinstance(listId,GetShopBaseClass): 
      args["listId"]=json.dumps(listId.__dict__)
    else:
      try:
        args["listId"]=json.dumps(listId)
      except (ValueError, AttributeError):
        args["listId"]=listId
    data = EmptyClass()
    data.args = args
    data.method = "getProductList"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getProductLists(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getProductLists"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getProducts(self, productCriteria):
    args = collections.OrderedDict()
    if isinstance(productCriteria,GetShopBaseClass): 
      args["productCriteria"]=json.dumps(productCriteria.__dict__)
    else:
      try:
        args["productCriteria"]=json.dumps(productCriteria)
      except (ValueError, AttributeError):
        args["productCriteria"]=productCriteria
    data = EmptyClass()
    data.args = args
    data.method = "getProducts"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getRandomProducts(self, fetchSize, ignoreProductId):
    args = collections.OrderedDict()
    if isinstance(fetchSize,GetShopBaseClass): 
      args["fetchSize"]=json.dumps(fetchSize.__dict__)
    else:
      try:
        args["fetchSize"]=json.dumps(fetchSize)
      except (ValueError, AttributeError):
        args["fetchSize"]=fetchSize
    if isinstance(ignoreProductId,GetShopBaseClass): 
      args["ignoreProductId"]=json.dumps(ignoreProductId.__dict__)
    else:
      try:
        args["ignoreProductId"]=json.dumps(ignoreProductId)
      except (ValueError, AttributeError):
        args["ignoreProductId"]=ignoreProductId
    data = EmptyClass()
    data.args = args
    data.method = "getRandomProducts"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def getTaxes(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getTaxes"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def removeProduct(self, productId):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    data = EmptyClass()
    data.args = args
    data.method = "removeProduct"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def saveProduct(self, product):
    args = collections.OrderedDict()
    if isinstance(product,GetShopBaseClass): 
      args["product"]=json.dumps(product.__dict__)
    else:
      try:
        args["product"]=json.dumps(product)
      except (ValueError, AttributeError):
        args["product"]=product
    data = EmptyClass()
    data.args = args
    data.method = "saveProduct"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def saveProductList(self, productList):
    args = collections.OrderedDict()
    if isinstance(productList,GetShopBaseClass): 
      args["productList"]=json.dumps(productList.__dict__)
    else:
      try:
        args["productList"]=json.dumps(productList)
      except (ValueError, AttributeError):
        args["productList"]=productList
    data = EmptyClass()
    data.args = args
    data.method = "saveProductList"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def search(self, searchWord, pageSize, page):
    args = collections.OrderedDict()
    if isinstance(searchWord,GetShopBaseClass): 
      args["searchWord"]=json.dumps(searchWord.__dict__)
    else:
      try:
        args["searchWord"]=json.dumps(searchWord)
      except (ValueError, AttributeError):
        args["searchWord"]=searchWord
    if isinstance(pageSize,GetShopBaseClass): 
      args["pageSize"]=json.dumps(pageSize.__dict__)
    else:
      try:
        args["pageSize"]=json.dumps(pageSize)
      except (ValueError, AttributeError):
        args["pageSize"]=pageSize
    if isinstance(page,GetShopBaseClass): 
      args["page"]=json.dumps(page.__dict__)
    else:
      try:
        args["page"]=json.dumps(page)
      except (ValueError, AttributeError):
        args["page"]=page
    data = EmptyClass()
    data.args = args
    data.method = "search"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def setMainImage(self, productId, imageId):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(imageId,GetShopBaseClass): 
      args["imageId"]=json.dumps(imageId.__dict__)
    else:
      try:
        args["imageId"]=json.dumps(imageId)
      except (ValueError, AttributeError):
        args["imageId"]=imageId
    data = EmptyClass()
    data.args = args
    data.method = "setMainImage"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def setProductDynamicPrice(self, productId, count):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(count,GetShopBaseClass): 
      args["count"]=json.dumps(count.__dict__)
    else:
      try:
        args["count"]=json.dumps(count)
      except (ValueError, AttributeError):
        args["count"]=count
    data = EmptyClass()
    data.args = args
    data.method = "setProductDynamicPrice"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def setTaxes(self, group):
    args = collections.OrderedDict()
    if isinstance(group,GetShopBaseClass): 
      args["group"]=json.dumps(group.__dict__)
    else:
      try:
        args["group"]=json.dumps(group)
      except (ValueError, AttributeError):
        args["group"]=group
    data = EmptyClass()
    data.args = args
    data.method = "setTaxes"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def translateEntries(self, entryIds):
    args = collections.OrderedDict()
    if isinstance(entryIds,GetShopBaseClass): 
      args["entryIds"]=json.dumps(entryIds.__dict__)
    else:
      try:
        args["entryIds"]=json.dumps(entryIds)
      except (ValueError, AttributeError):
        args["entryIds"]=entryIds
    data = EmptyClass()
    data.args = args
    data.method = "translateEntries"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

  def updateAttributePool(self, groups):
    args = collections.OrderedDict()
    if isinstance(groups,GetShopBaseClass): 
      args["groups"]=json.dumps(groups.__dict__)
    else:
      try:
        args["groups"]=json.dumps(groups)
      except (ValueError, AttributeError):
        args["groups"]=groups
    data = EmptyClass()
    data.args = args
    data.method = "updateAttributePool"
    data.interfaceName = "core.productmanager.IProductManager"
    return self.communicationHelper.sendMessage(data)

class ReportingManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def getAllEventsFromSession(self, startDate, stopDate, searchSessionId):
    args = collections.OrderedDict()
    if isinstance(startDate,GetShopBaseClass): 
      args["startDate"]=json.dumps(startDate.__dict__)
    else:
      try:
        args["startDate"]=json.dumps(startDate)
      except (ValueError, AttributeError):
        args["startDate"]=startDate
    if isinstance(stopDate,GetShopBaseClass): 
      args["stopDate"]=json.dumps(stopDate.__dict__)
    else:
      try:
        args["stopDate"]=json.dumps(stopDate)
      except (ValueError, AttributeError):
        args["stopDate"]=stopDate
    if isinstance(searchSessionId,GetShopBaseClass): 
      args["searchSessionId"]=json.dumps(searchSessionId.__dict__)
    else:
      try:
        args["searchSessionId"]=json.dumps(searchSessionId)
      except (ValueError, AttributeError):
        args["searchSessionId"]=searchSessionId
    data = EmptyClass()
    data.args = args
    data.method = "getAllEventsFromSession"
    data.interfaceName = "core.reportingmanager.IReportingManager"
    return self.communicationHelper.sendMessage(data)

  def getConnectedUsers(self, startdate, stopDate, filter):
    args = collections.OrderedDict()
    if isinstance(startdate,GetShopBaseClass): 
      args["startdate"]=json.dumps(startdate.__dict__)
    else:
      try:
        args["startdate"]=json.dumps(startdate)
      except (ValueError, AttributeError):
        args["startdate"]=startdate
    if isinstance(stopDate,GetShopBaseClass): 
      args["stopDate"]=json.dumps(stopDate.__dict__)
    else:
      try:
        args["stopDate"]=json.dumps(stopDate)
      except (ValueError, AttributeError):
        args["stopDate"]=stopDate
    if isinstance(filter,GetShopBaseClass): 
      args["filter"]=json.dumps(filter.__dict__)
    else:
      try:
        args["filter"]=json.dumps(filter)
      except (ValueError, AttributeError):
        args["filter"]=filter
    data = EmptyClass()
    data.args = args
    data.method = "getConnectedUsers"
    data.interfaceName = "core.reportingmanager.IReportingManager"
    return self.communicationHelper.sendMessage(data)

  def getOrdersCreated(self, startDate, stopDate):
    args = collections.OrderedDict()
    if isinstance(startDate,GetShopBaseClass): 
      args["startDate"]=json.dumps(startDate.__dict__)
    else:
      try:
        args["startDate"]=json.dumps(startDate)
      except (ValueError, AttributeError):
        args["startDate"]=startDate
    if isinstance(stopDate,GetShopBaseClass): 
      args["stopDate"]=json.dumps(stopDate.__dict__)
    else:
      try:
        args["stopDate"]=json.dumps(stopDate)
      except (ValueError, AttributeError):
        args["stopDate"]=stopDate
    data = EmptyClass()
    data.args = args
    data.method = "getOrdersCreated"
    data.interfaceName = "core.reportingmanager.IReportingManager"
    return self.communicationHelper.sendMessage(data)

  def getPageViews(self, startDate, stopDate):
    args = collections.OrderedDict()
    if isinstance(startDate,GetShopBaseClass): 
      args["startDate"]=json.dumps(startDate.__dict__)
    else:
      try:
        args["startDate"]=json.dumps(startDate)
      except (ValueError, AttributeError):
        args["startDate"]=startDate
    if isinstance(stopDate,GetShopBaseClass): 
      args["stopDate"]=json.dumps(stopDate.__dict__)
    else:
      try:
        args["stopDate"]=json.dumps(stopDate)
      except (ValueError, AttributeError):
        args["stopDate"]=stopDate
    data = EmptyClass()
    data.args = args
    data.method = "getPageViews"
    data.interfaceName = "core.reportingmanager.IReportingManager"
    return self.communicationHelper.sendMessage(data)

  def getProductViewed(self, startDate, stopDate):
    args = collections.OrderedDict()
    if isinstance(startDate,GetShopBaseClass): 
      args["startDate"]=json.dumps(startDate.__dict__)
    else:
      try:
        args["startDate"]=json.dumps(startDate)
      except (ValueError, AttributeError):
        args["startDate"]=startDate
    if isinstance(stopDate,GetShopBaseClass): 
      args["stopDate"]=json.dumps(stopDate.__dict__)
    else:
      try:
        args["stopDate"]=json.dumps(stopDate)
      except (ValueError, AttributeError):
        args["stopDate"]=stopDate
    data = EmptyClass()
    data.args = args
    data.method = "getProductViewed"
    data.interfaceName = "core.reportingmanager.IReportingManager"
    return self.communicationHelper.sendMessage(data)

  def getReport(self, startDate, stopDate, type):
    args = collections.OrderedDict()
    if isinstance(startDate,GetShopBaseClass): 
      args["startDate"]=json.dumps(startDate.__dict__)
    else:
      try:
        args["startDate"]=json.dumps(startDate)
      except (ValueError, AttributeError):
        args["startDate"]=startDate
    if isinstance(stopDate,GetShopBaseClass): 
      args["stopDate"]=json.dumps(stopDate.__dict__)
    else:
      try:
        args["stopDate"]=json.dumps(stopDate)
      except (ValueError, AttributeError):
        args["stopDate"]=stopDate
    if isinstance(type,GetShopBaseClass): 
      args["type"]=json.dumps(type.__dict__)
    else:
      try:
        args["type"]=json.dumps(type)
      except (ValueError, AttributeError):
        args["type"]=type
    data = EmptyClass()
    data.args = args
    data.method = "getReport"
    data.interfaceName = "core.reportingmanager.IReportingManager"
    return self.communicationHelper.sendMessage(data)

  def getUserLoggedOn(self, startDate, stopDate):
    args = collections.OrderedDict()
    if isinstance(startDate,GetShopBaseClass): 
      args["startDate"]=json.dumps(startDate.__dict__)
    else:
      try:
        args["startDate"]=json.dumps(startDate)
      except (ValueError, AttributeError):
        args["startDate"]=startDate
    if isinstance(stopDate,GetShopBaseClass): 
      args["stopDate"]=json.dumps(stopDate.__dict__)
    else:
      try:
        args["stopDate"]=json.dumps(stopDate)
      except (ValueError, AttributeError):
        args["stopDate"]=stopDate
    data = EmptyClass()
    data.args = args
    data.method = "getUserLoggedOn"
    data.interfaceName = "core.reportingmanager.IReportingManager"
    return self.communicationHelper.sendMessage(data)

class SedoxProductManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addCreditToSlave(self, slaveId, amount):
    args = collections.OrderedDict()
    if isinstance(slaveId,GetShopBaseClass): 
      args["slaveId"]=json.dumps(slaveId.__dict__)
    else:
      try:
        args["slaveId"]=json.dumps(slaveId)
      except (ValueError, AttributeError):
        args["slaveId"]=slaveId
    if isinstance(amount,GetShopBaseClass): 
      args["amount"]=json.dumps(amount.__dict__)
    else:
      try:
        args["amount"]=json.dumps(amount)
      except (ValueError, AttributeError):
        args["amount"]=amount
    data = EmptyClass()
    data.args = args
    data.method = "addCreditToSlave"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def addFileToProduct(self, base64EncodedFile, fileName, fileType, productId):
    args = collections.OrderedDict()
    if isinstance(base64EncodedFile,GetShopBaseClass): 
      args["base64EncodedFile"]=json.dumps(base64EncodedFile.__dict__)
    else:
      try:
        args["base64EncodedFile"]=json.dumps(base64EncodedFile)
      except (ValueError, AttributeError):
        args["base64EncodedFile"]=base64EncodedFile
    if isinstance(fileName,GetShopBaseClass): 
      args["fileName"]=json.dumps(fileName.__dict__)
    else:
      try:
        args["fileName"]=json.dumps(fileName)
      except (ValueError, AttributeError):
        args["fileName"]=fileName
    if isinstance(fileType,GetShopBaseClass): 
      args["fileType"]=json.dumps(fileType.__dict__)
    else:
      try:
        args["fileType"]=json.dumps(fileType)
      except (ValueError, AttributeError):
        args["fileType"]=fileType
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    data = EmptyClass()
    data.args = args
    data.method = "addFileToProduct"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def addSlaveToUser(self, masterUserId, slaveUserId):
    args = collections.OrderedDict()
    if isinstance(masterUserId,GetShopBaseClass): 
      args["masterUserId"]=json.dumps(masterUserId.__dict__)
    else:
      try:
        args["masterUserId"]=json.dumps(masterUserId)
      except (ValueError, AttributeError):
        args["masterUserId"]=masterUserId
    if isinstance(slaveUserId,GetShopBaseClass): 
      args["slaveUserId"]=json.dumps(slaveUserId.__dict__)
    else:
      try:
        args["slaveUserId"]=json.dumps(slaveUserId)
      except (ValueError, AttributeError):
        args["slaveUserId"]=slaveUserId
    data = EmptyClass()
    data.args = args
    data.method = "addSlaveToUser"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def addUserCredit(self, id, description, amount):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    if isinstance(description,GetShopBaseClass): 
      args["description"]=json.dumps(description.__dict__)
    else:
      try:
        args["description"]=json.dumps(description)
      except (ValueError, AttributeError):
        args["description"]=description
    if isinstance(amount,GetShopBaseClass): 
      args["amount"]=json.dumps(amount.__dict__)
    else:
      try:
        args["amount"]=json.dumps(amount)
      except (ValueError, AttributeError):
        args["amount"]=amount
    data = EmptyClass()
    data.args = args
    data.method = "addUserCredit"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def changeDeveloperStatus(self, userId, disabled):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(disabled,GetShopBaseClass): 
      args["disabled"]=json.dumps(disabled.__dict__)
    else:
      try:
        args["disabled"]=json.dumps(disabled)
      except (ValueError, AttributeError):
        args["disabled"]=disabled
    data = EmptyClass()
    data.args = args
    data.method = "changeDeveloperStatus"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def createSedoxProduct(self, sedoxProduct, base64encodedOriginalFile, originalFileName, forSlaveId, origin):
    args = collections.OrderedDict()
    if isinstance(sedoxProduct,GetShopBaseClass): 
      args["sedoxProduct"]=json.dumps(sedoxProduct.__dict__)
    else:
      try:
        args["sedoxProduct"]=json.dumps(sedoxProduct)
      except (ValueError, AttributeError):
        args["sedoxProduct"]=sedoxProduct
    if isinstance(base64encodedOriginalFile,GetShopBaseClass): 
      args["base64encodedOriginalFile"]=json.dumps(base64encodedOriginalFile.__dict__)
    else:
      try:
        args["base64encodedOriginalFile"]=json.dumps(base64encodedOriginalFile)
      except (ValueError, AttributeError):
        args["base64encodedOriginalFile"]=base64encodedOriginalFile
    if isinstance(originalFileName,GetShopBaseClass): 
      args["originalFileName"]=json.dumps(originalFileName.__dict__)
    else:
      try:
        args["originalFileName"]=json.dumps(originalFileName)
      except (ValueError, AttributeError):
        args["originalFileName"]=originalFileName
    if isinstance(forSlaveId,GetShopBaseClass): 
      args["forSlaveId"]=json.dumps(forSlaveId.__dict__)
    else:
      try:
        args["forSlaveId"]=json.dumps(forSlaveId)
      except (ValueError, AttributeError):
        args["forSlaveId"]=forSlaveId
    if isinstance(origin,GetShopBaseClass): 
      args["origin"]=json.dumps(origin.__dict__)
    else:
      try:
        args["origin"]=json.dumps(origin)
      except (ValueError, AttributeError):
        args["origin"]=origin
    data = EmptyClass()
    data.args = args
    data.method = "createSedoxProduct"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def getAllUsersWithNegativeCreditLimit(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllUsersWithNegativeCreditLimit"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def getDevelopers(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getDevelopers"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def getExtraInformationForFile(self, productId, fileId):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(fileId,GetShopBaseClass): 
      args["fileId"]=json.dumps(fileId.__dict__)
    else:
      try:
        args["fileId"]=json.dumps(fileId)
      except (ValueError, AttributeError):
        args["fileId"]=fileId
    data = EmptyClass()
    data.args = args
    data.method = "getExtraInformationForFile"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def getProductById(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getProductById"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def getProductsByDaysBack(self, day):
    args = collections.OrderedDict()
    if isinstance(day,GetShopBaseClass): 
      args["day"]=json.dumps(day.__dict__)
    else:
      try:
        args["day"]=json.dumps(day)
      except (ValueError, AttributeError):
        args["day"]=day
    data = EmptyClass()
    data.args = args
    data.method = "getProductsByDaysBack"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def getProductsFirstUploadedByCurrentUser(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getProductsFirstUploadedByCurrentUser"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def getSedoxProductByMd5Sum(self, md5sum):
    args = collections.OrderedDict()
    if isinstance(md5sum,GetShopBaseClass): 
      args["md5sum"]=json.dumps(md5sum.__dict__)
    else:
      try:
        args["md5sum"]=json.dumps(md5sum)
      except (ValueError, AttributeError):
        args["md5sum"]=md5sum
    data = EmptyClass()
    data.args = args
    data.method = "getSedoxProductByMd5Sum"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def getSedoxUserAccount(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getSedoxUserAccount"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def getSedoxUserAccountById(self, userid):
    args = collections.OrderedDict()
    if isinstance(userid,GetShopBaseClass): 
      args["userid"]=json.dumps(userid.__dict__)
    else:
      try:
        args["userid"]=json.dumps(userid)
      except (ValueError, AttributeError):
        args["userid"]=userid
    data = EmptyClass()
    data.args = args
    data.method = "getSedoxUserAccountById"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def getSlaves(self, masterUserId):
    args = collections.OrderedDict()
    if isinstance(masterUserId,GetShopBaseClass): 
      args["masterUserId"]=json.dumps(masterUserId.__dict__)
    else:
      try:
        args["masterUserId"]=json.dumps(masterUserId)
      except (ValueError, AttributeError):
        args["masterUserId"]=masterUserId
    data = EmptyClass()
    data.args = args
    data.method = "getSlaves"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def login(self, emailAddress, password):
    args = collections.OrderedDict()
    if isinstance(emailAddress,GetShopBaseClass): 
      args["emailAddress"]=json.dumps(emailAddress.__dict__)
    else:
      try:
        args["emailAddress"]=json.dumps(emailAddress)
      except (ValueError, AttributeError):
        args["emailAddress"]=emailAddress
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "login"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def notifyForCustomer(self, productId, extraText):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(extraText,GetShopBaseClass): 
      args["extraText"]=json.dumps(extraText.__dict__)
    else:
      try:
        args["extraText"]=json.dumps(extraText)
      except (ValueError, AttributeError):
        args["extraText"]=extraText
    data = EmptyClass()
    data.args = args
    data.method = "notifyForCustomer"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def purchaseOnlyForCustomer(self, productId, files):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(files,GetShopBaseClass): 
      args["files"]=json.dumps(files.__dict__)
    else:
      try:
        args["files"]=json.dumps(files)
      except (ValueError, AttributeError):
        args["files"]=files
    data = EmptyClass()
    data.args = args
    data.method = "purchaseOnlyForCustomer"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def purchaseProduct(self, productId, files):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(files,GetShopBaseClass): 
      args["files"]=json.dumps(files.__dict__)
    else:
      try:
        args["files"]=json.dumps(files)
      except (ValueError, AttributeError):
        args["files"]=files
    data = EmptyClass()
    data.args = args
    data.method = "purchaseProduct"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def removeBinaryFileFromProduct(self, productId, fileId):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(fileId,GetShopBaseClass): 
      args["fileId"]=json.dumps(fileId.__dict__)
    else:
      try:
        args["fileId"]=json.dumps(fileId)
      except (ValueError, AttributeError):
        args["fileId"]=fileId
    data = EmptyClass()
    data.args = args
    data.method = "removeBinaryFileFromProduct"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def requestSpecialFile(self, productId, comment):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(comment,GetShopBaseClass): 
      args["comment"]=json.dumps(comment.__dict__)
    else:
      try:
        args["comment"]=json.dumps(comment)
      except (ValueError, AttributeError):
        args["comment"]=comment
    data = EmptyClass()
    data.args = args
    data.method = "requestSpecialFile"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def search(self, search):
    args = collections.OrderedDict()
    if isinstance(search,GetShopBaseClass): 
      args["search"]=json.dumps(search.__dict__)
    else:
      try:
        args["search"]=json.dumps(search)
      except (ValueError, AttributeError):
        args["search"]=search
    data = EmptyClass()
    data.args = args
    data.method = "search"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def searchForUsers(self, searchString):
    args = collections.OrderedDict()
    if isinstance(searchString,GetShopBaseClass): 
      args["searchString"]=json.dumps(searchString.__dict__)
    else:
      try:
        args["searchString"]=json.dumps(searchString)
      except (ValueError, AttributeError):
        args["searchString"]=searchString
    data = EmptyClass()
    data.args = args
    data.method = "searchForUsers"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def sendProductByMail(self, productId, extraText, files):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(extraText,GetShopBaseClass): 
      args["extraText"]=json.dumps(extraText.__dict__)
    else:
      try:
        args["extraText"]=json.dumps(extraText)
      except (ValueError, AttributeError):
        args["extraText"]=extraText
    if isinstance(files,GetShopBaseClass): 
      args["files"]=json.dumps(files.__dict__)
    else:
      try:
        args["files"]=json.dumps(files)
      except (ValueError, AttributeError):
        args["files"]=files
    data = EmptyClass()
    data.args = args
    data.method = "sendProductByMail"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def setChecksum(self, productId, checksum):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(checksum,GetShopBaseClass): 
      args["checksum"]=json.dumps(checksum.__dict__)
    else:
      try:
        args["checksum"]=json.dumps(checksum)
      except (ValueError, AttributeError):
        args["checksum"]=checksum
    data = EmptyClass()
    data.args = args
    data.method = "setChecksum"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def setExtraInformationForFile(self, productId, fileId, text):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(fileId,GetShopBaseClass): 
      args["fileId"]=json.dumps(fileId.__dict__)
    else:
      try:
        args["fileId"]=json.dumps(fileId)
      except (ValueError, AttributeError):
        args["fileId"]=fileId
    if isinstance(text,GetShopBaseClass): 
      args["text"]=json.dumps(text.__dict__)
    else:
      try:
        args["text"]=json.dumps(text)
      except (ValueError, AttributeError):
        args["text"]=text
    data = EmptyClass()
    data.args = args
    data.method = "setExtraInformationForFile"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def sync(self, option):
    args = collections.OrderedDict()
    if isinstance(option,GetShopBaseClass): 
      args["option"]=json.dumps(option.__dict__)
    else:
      try:
        args["option"]=json.dumps(option)
      except (ValueError, AttributeError):
        args["option"]=option
    data = EmptyClass()
    data.args = args
    data.method = "sync"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def toggleAllowNegativeCredit(self, userId, allow):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(allow,GetShopBaseClass): 
      args["allow"]=json.dumps(allow.__dict__)
    else:
      try:
        args["allow"]=json.dumps(allow)
      except (ValueError, AttributeError):
        args["allow"]=allow
    data = EmptyClass()
    data.args = args
    data.method = "toggleAllowNegativeCredit"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def toggleAllowWindowsApp(self, userId, allow):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(allow,GetShopBaseClass): 
      args["allow"]=json.dumps(allow.__dict__)
    else:
      try:
        args["allow"]=json.dumps(allow)
      except (ValueError, AttributeError):
        args["allow"]=allow
    data = EmptyClass()
    data.args = args
    data.method = "toggleAllowWindowsApp"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def toggleIsNorwegian(self, userId, isNorwegian):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(isNorwegian,GetShopBaseClass): 
      args["isNorwegian"]=json.dumps(isNorwegian.__dict__)
    else:
      try:
        args["isNorwegian"]=json.dumps(isNorwegian)
      except (ValueError, AttributeError):
        args["isNorwegian"]=isNorwegian
    data = EmptyClass()
    data.args = args
    data.method = "toggleIsNorwegian"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def togglePassiveSlaveMode(self, userId, toggle):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(toggle,GetShopBaseClass): 
      args["toggle"]=json.dumps(toggle.__dict__)
    else:
      try:
        args["toggle"]=json.dumps(toggle)
      except (ValueError, AttributeError):
        args["toggle"]=toggle
    data = EmptyClass()
    data.args = args
    data.method = "togglePassiveSlaveMode"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def toggleSaleableProduct(self, productId, saleable):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(saleable,GetShopBaseClass): 
      args["saleable"]=json.dumps(saleable.__dict__)
    else:
      try:
        args["saleable"]=json.dumps(saleable)
      except (ValueError, AttributeError):
        args["saleable"]=saleable
    data = EmptyClass()
    data.args = args
    data.method = "toggleSaleableProduct"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

  def toggleStartStop(self, productId, toggle):
    args = collections.OrderedDict()
    if isinstance(productId,GetShopBaseClass): 
      args["productId"]=json.dumps(productId.__dict__)
    else:
      try:
        args["productId"]=json.dumps(productId)
      except (ValueError, AttributeError):
        args["productId"]=productId
    if isinstance(toggle,GetShopBaseClass): 
      args["toggle"]=json.dumps(toggle.__dict__)
    else:
      try:
        args["toggle"]=json.dumps(toggle)
      except (ValueError, AttributeError):
        args["toggle"]=toggle
    data = EmptyClass()
    data.args = args
    data.method = "toggleStartStop"
    data.interfaceName = "core.sedox.ISedoxProductManager"
    return self.communicationHelper.sendMessage(data)

class StoreApplicationInstancePool(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def createNewInstance(self, applicationId):
    args = collections.OrderedDict()
    if isinstance(applicationId,GetShopBaseClass): 
      args["applicationId"]=json.dumps(applicationId.__dict__)
    else:
      try:
        args["applicationId"]=json.dumps(applicationId)
      except (ValueError, AttributeError):
        args["applicationId"]=applicationId
    data = EmptyClass()
    data.args = args
    data.method = "createNewInstance"
    data.interfaceName = "core.applications.IStoreApplicationInstancePool"
    return self.communicationHelper.sendMessage(data)

  def getApplicationInstance(self, applicationInstanceId):
    args = collections.OrderedDict()
    if isinstance(applicationInstanceId,GetShopBaseClass): 
      args["applicationInstanceId"]=json.dumps(applicationInstanceId.__dict__)
    else:
      try:
        args["applicationInstanceId"]=json.dumps(applicationInstanceId)
      except (ValueError, AttributeError):
        args["applicationInstanceId"]=applicationInstanceId
    data = EmptyClass()
    data.args = args
    data.method = "getApplicationInstance"
    data.interfaceName = "core.applications.IStoreApplicationInstancePool"
    return self.communicationHelper.sendMessage(data)

  def setApplicationSettings(self, settings):
    args = collections.OrderedDict()
    if isinstance(settings,GetShopBaseClass): 
      args["settings"]=json.dumps(settings.__dict__)
    else:
      try:
        args["settings"]=json.dumps(settings)
      except (ValueError, AttributeError):
        args["settings"]=settings
    data = EmptyClass()
    data.args = args
    data.method = "setApplicationSettings"
    data.interfaceName = "core.applications.IStoreApplicationInstancePool"
    return self.communicationHelper.sendMessage(data)

class StoreApplicationPool(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def activateApplication(self, applicationId):
    args = collections.OrderedDict()
    if isinstance(applicationId,GetShopBaseClass): 
      args["applicationId"]=json.dumps(applicationId.__dict__)
    else:
      try:
        args["applicationId"]=json.dumps(applicationId)
      except (ValueError, AttributeError):
        args["applicationId"]=applicationId
    data = EmptyClass()
    data.args = args
    data.method = "activateApplication"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def activateModule(self, module):
    args = collections.OrderedDict()
    if isinstance(module,GetShopBaseClass): 
      args["module"]=json.dumps(module.__dict__)
    else:
      try:
        args["module"]=json.dumps(module)
      except (ValueError, AttributeError):
        args["module"]=module
    data = EmptyClass()
    data.args = args
    data.method = "activateModule"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def deactivateApplication(self, applicationId):
    args = collections.OrderedDict()
    if isinstance(applicationId,GetShopBaseClass): 
      args["applicationId"]=json.dumps(applicationId.__dict__)
    else:
      try:
        args["applicationId"]=json.dumps(applicationId)
      except (ValueError, AttributeError):
        args["applicationId"]=applicationId
    data = EmptyClass()
    data.args = args
    data.method = "deactivateApplication"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def getActivatedModules(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getActivatedModules"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def getActivatedPaymentApplications(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getActivatedPaymentApplications"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def getAllAvailableModules(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllAvailableModules"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def getApplication(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getApplication"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def getApplications(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getApplications"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def getAvailableApplications(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAvailableApplications"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def getAvailableApplicationsThatIsNotActivated(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAvailableApplicationsThatIsNotActivated"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def getAvailableThemeApplications(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAvailableThemeApplications"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def getPaymentSettingsApplication(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getPaymentSettingsApplication"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def getShippingApplications(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getShippingApplications"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def getThemeApplication(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getThemeApplication"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def setSetting(self, applicationId, settings):
    args = collections.OrderedDict()
    if isinstance(applicationId,GetShopBaseClass): 
      args["applicationId"]=json.dumps(applicationId.__dict__)
    else:
      try:
        args["applicationId"]=json.dumps(applicationId)
      except (ValueError, AttributeError):
        args["applicationId"]=applicationId
    if isinstance(settings,GetShopBaseClass): 
      args["settings"]=json.dumps(settings.__dict__)
    else:
      try:
        args["settings"]=json.dumps(settings)
      except (ValueError, AttributeError):
        args["settings"]=settings
    data = EmptyClass()
    data.args = args
    data.method = "setSetting"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

  def setThemeApplication(self, applicationId):
    args = collections.OrderedDict()
    if isinstance(applicationId,GetShopBaseClass): 
      args["applicationId"]=json.dumps(applicationId.__dict__)
    else:
      try:
        args["applicationId"]=json.dumps(applicationId)
      except (ValueError, AttributeError):
        args["applicationId"]=applicationId
    data = EmptyClass()
    data.args = args
    data.method = "setThemeApplication"
    data.interfaceName = "core.applications.IStoreApplicationPool"
    return self.communicationHelper.sendMessage(data)

class StoreManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def createStore(self, hostname, email, password, notify):
    args = collections.OrderedDict()
    if isinstance(hostname,GetShopBaseClass): 
      args["hostname"]=json.dumps(hostname.__dict__)
    else:
      try:
        args["hostname"]=json.dumps(hostname)
      except (ValueError, AttributeError):
        args["hostname"]=hostname
    if isinstance(email,GetShopBaseClass): 
      args["email"]=json.dumps(email.__dict__)
    else:
      try:
        args["email"]=json.dumps(email)
      except (ValueError, AttributeError):
        args["email"]=email
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    if isinstance(notify,GetShopBaseClass): 
      args["notify"]=json.dumps(notify.__dict__)
    else:
      try:
        args["notify"]=json.dumps(notify)
      except (ValueError, AttributeError):
        args["notify"]=notify
    data = EmptyClass()
    data.args = args
    data.method = "createStore"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def delete(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "delete"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def enableExtendedMode(self, toggle, password):
    args = collections.OrderedDict()
    if isinstance(toggle,GetShopBaseClass): 
      args["toggle"]=json.dumps(toggle.__dict__)
    else:
      try:
        args["toggle"]=json.dumps(toggle)
      except (ValueError, AttributeError):
        args["toggle"]=toggle
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "enableExtendedMode"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def enableSMSAccess(self, toggle, password):
    args = collections.OrderedDict()
    if isinstance(toggle,GetShopBaseClass): 
      args["toggle"]=json.dumps(toggle.__dict__)
    else:
      try:
        args["toggle"]=json.dumps(toggle)
      except (ValueError, AttributeError):
        args["toggle"]=toggle
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "enableSMSAccess"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def generateStoreId(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "generateStoreId"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def getMyStore(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getMyStore"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def getStoreId(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getStoreId"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def initializeStore(self, webAddress, initSessionId):
    args = collections.OrderedDict()
    if isinstance(webAddress,GetShopBaseClass): 
      args["webAddress"]=json.dumps(webAddress.__dict__)
    else:
      try:
        args["webAddress"]=json.dumps(webAddress)
      except (ValueError, AttributeError):
        args["webAddress"]=webAddress
    if isinstance(initSessionId,GetShopBaseClass): 
      args["initSessionId"]=json.dumps(initSessionId.__dict__)
    else:
      try:
        args["initSessionId"]=json.dumps(initSessionId)
      except (ValueError, AttributeError):
        args["initSessionId"]=initSessionId
    data = EmptyClass()
    data.args = args
    data.method = "initializeStore"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def isAddressTaken(self, address):
    args = collections.OrderedDict()
    if isinstance(address,GetShopBaseClass): 
      args["address"]=json.dumps(address.__dict__)
    else:
      try:
        args["address"]=json.dumps(address)
      except (ValueError, AttributeError):
        args["address"]=address
    data = EmptyClass()
    data.args = args
    data.method = "isAddressTaken"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def removeDomainName(self, domainName):
    args = collections.OrderedDict()
    if isinstance(domainName,GetShopBaseClass): 
      args["domainName"]=json.dumps(domainName.__dict__)
    else:
      try:
        args["domainName"]=json.dumps(domainName)
      except (ValueError, AttributeError):
        args["domainName"]=domainName
    data = EmptyClass()
    data.args = args
    data.method = "removeDomainName"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def saveStore(self, config):
    args = collections.OrderedDict()
    if isinstance(config,GetShopBaseClass): 
      args["config"]=json.dumps(config.__dict__)
    else:
      try:
        args["config"]=json.dumps(config)
      except (ValueError, AttributeError):
        args["config"]=config
    data = EmptyClass()
    data.args = args
    data.method = "saveStore"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def setDeepFreeze(self, mode, password):
    args = collections.OrderedDict()
    if isinstance(mode,GetShopBaseClass): 
      args["mode"]=json.dumps(mode.__dict__)
    else:
      try:
        args["mode"]=json.dumps(mode)
      except (ValueError, AttributeError):
        args["mode"]=mode
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "setDeepFreeze"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def setIntroductionRead(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "setIntroductionRead"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def setIsTemplate(self, storeId, isTemplate):
    args = collections.OrderedDict()
    if isinstance(storeId,GetShopBaseClass): 
      args["storeId"]=json.dumps(storeId.__dict__)
    else:
      try:
        args["storeId"]=json.dumps(storeId)
      except (ValueError, AttributeError):
        args["storeId"]=storeId
    if isinstance(isTemplate,GetShopBaseClass): 
      args["isTemplate"]=json.dumps(isTemplate.__dict__)
    else:
      try:
        args["isTemplate"]=json.dumps(isTemplate)
      except (ValueError, AttributeError):
        args["isTemplate"]=isTemplate
    data = EmptyClass()
    data.args = args
    data.method = "setIsTemplate"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def setPrimaryDomainName(self, domainName):
    args = collections.OrderedDict()
    if isinstance(domainName,GetShopBaseClass): 
      args["domainName"]=json.dumps(domainName.__dict__)
    else:
      try:
        args["domainName"]=json.dumps(domainName)
      except (ValueError, AttributeError):
        args["domainName"]=domainName
    data = EmptyClass()
    data.args = args
    data.method = "setPrimaryDomainName"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

  def setSessionLanguage(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "setSessionLanguage"
    data.interfaceName = "core.storemanager.IStoreManager"
    return self.communicationHelper.sendMessage(data)

class UserManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def addComment(self, userId, comment):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(comment,GetShopBaseClass): 
      args["comment"]=json.dumps(comment.__dict__)
    else:
      try:
        args["comment"]=json.dumps(comment)
      except (ValueError, AttributeError):
        args["comment"]=comment
    data = EmptyClass()
    data.args = args
    data.method = "addComment"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def addUserPrivilege(self, userId, managerName, managerFunction):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(managerName,GetShopBaseClass): 
      args["managerName"]=json.dumps(managerName.__dict__)
    else:
      try:
        args["managerName"]=json.dumps(managerName)
      except (ValueError, AttributeError):
        args["managerName"]=managerName
    if isinstance(managerFunction,GetShopBaseClass): 
      args["managerFunction"]=json.dumps(managerFunction.__dict__)
    else:
      try:
        args["managerFunction"]=json.dumps(managerFunction)
      except (ValueError, AttributeError):
        args["managerFunction"]=managerFunction
    data = EmptyClass()
    data.args = args
    data.method = "addUserPrivilege"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def cancelImpersonating(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "cancelImpersonating"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def checkUserNameAndPassword(self, username, password):
    args = collections.OrderedDict()
    if isinstance(username,GetShopBaseClass): 
      args["username"]=json.dumps(username.__dict__)
    else:
      try:
        args["username"]=json.dumps(username)
      except (ValueError, AttributeError):
        args["username"]=username
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "checkUserNameAndPassword"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def createUser(self, user):
    args = collections.OrderedDict()
    if isinstance(user,GetShopBaseClass): 
      args["user"]=json.dumps(user.__dict__)
    else:
      try:
        args["user"]=json.dumps(user)
      except (ValueError, AttributeError):
        args["user"]=user
    data = EmptyClass()
    data.args = args
    data.method = "createUser"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def deleteUser(self, userId):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    data = EmptyClass()
    data.args = args
    data.method = "deleteUser"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def doEmailExists(self, email):
    args = collections.OrderedDict()
    if isinstance(email,GetShopBaseClass): 
      args["email"]=json.dumps(email.__dict__)
    else:
      try:
        args["email"]=json.dumps(email)
      except (ValueError, AttributeError):
        args["email"]=email
    data = EmptyClass()
    data.args = args
    data.method = "doEmailExists"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def doesUserExistsOnReferenceNumber(self, number):
    args = collections.OrderedDict()
    if isinstance(number,GetShopBaseClass): 
      args["number"]=json.dumps(number.__dict__)
    else:
      try:
        args["number"]=json.dumps(number)
      except (ValueError, AttributeError):
        args["number"]=number
    data = EmptyClass()
    data.args = args
    data.method = "doesUserExistsOnReferenceNumber"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def findUsers(self, searchCriteria):
    args = collections.OrderedDict()
    if isinstance(searchCriteria,GetShopBaseClass): 
      args["searchCriteria"]=json.dumps(searchCriteria.__dict__)
    else:
      try:
        args["searchCriteria"]=json.dumps(searchCriteria)
      except (ValueError, AttributeError):
        args["searchCriteria"]=searchCriteria
    data = EmptyClass()
    data.args = args
    data.method = "findUsers"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def getAdministratorCount(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAdministratorCount"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def getAllGroups(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllGroups"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def getAllUsers(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getAllUsers"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def getAllUsersWithCommentToApp(self, appId):
    args = collections.OrderedDict()
    if isinstance(appId,GetShopBaseClass): 
      args["appId"]=json.dumps(appId.__dict__)
    else:
      try:
        args["appId"]=json.dumps(appId)
      except (ValueError, AttributeError):
        args["appId"]=appId
    data = EmptyClass()
    data.args = args
    data.method = "getAllUsersWithCommentToApp"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def getCustomersCount(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getCustomersCount"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def getEditorCount(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getEditorCount"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def getLoggedOnUser(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "getLoggedOnUser"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def getLogins(self, year):
    args = collections.OrderedDict()
    if isinstance(year,GetShopBaseClass): 
      args["year"]=json.dumps(year.__dict__)
    else:
      try:
        args["year"]=json.dumps(year)
      except (ValueError, AttributeError):
        args["year"]=year
    data = EmptyClass()
    data.args = args
    data.method = "getLogins"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def getUserById(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getUserById"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def getUserList(self, userIds):
    args = collections.OrderedDict()
    if isinstance(userIds,GetShopBaseClass): 
      args["userIds"]=json.dumps(userIds.__dict__)
    else:
      try:
        args["userIds"]=json.dumps(userIds)
      except (ValueError, AttributeError):
        args["userIds"]=userIds
    data = EmptyClass()
    data.args = args
    data.method = "getUserList"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def impersonateUser(self, userId):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    data = EmptyClass()
    data.args = args
    data.method = "impersonateUser"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def isCaptain(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "isCaptain"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def isImpersonating(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "isImpersonating"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def isLoggedIn(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "isLoggedIn"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def logOn(self, username, password):
    args = collections.OrderedDict()
    if isinstance(username,GetShopBaseClass): 
      args["username"]=json.dumps(username.__dict__)
    else:
      try:
        args["username"]=json.dumps(username)
      except (ValueError, AttributeError):
        args["username"]=username
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "logOn"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def loginWithPincode(self, username, password, pinCode):
    args = collections.OrderedDict()
    if isinstance(username,GetShopBaseClass): 
      args["username"]=json.dumps(username.__dict__)
    else:
      try:
        args["username"]=json.dumps(username)
      except (ValueError, AttributeError):
        args["username"]=username
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    if isinstance(pinCode,GetShopBaseClass): 
      args["pinCode"]=json.dumps(pinCode.__dict__)
    else:
      try:
        args["pinCode"]=json.dumps(pinCode)
      except (ValueError, AttributeError):
        args["pinCode"]=pinCode
    data = EmptyClass()
    data.args = args
    data.method = "loginWithPincode"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def logonUsingKey(self, logonKey):
    args = collections.OrderedDict()
    if isinstance(logonKey,GetShopBaseClass): 
      args["logonKey"]=json.dumps(logonKey.__dict__)
    else:
      try:
        args["logonKey"]=json.dumps(logonKey)
      except (ValueError, AttributeError):
        args["logonKey"]=logonKey
    data = EmptyClass()
    data.args = args
    data.method = "logonUsingKey"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def logout(self):
    args = collections.OrderedDict()
    data = EmptyClass()
    data.args = args
    data.method = "logout"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def removeComment(self, userId, commentId):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(commentId,GetShopBaseClass): 
      args["commentId"]=json.dumps(commentId.__dict__)
    else:
      try:
        args["commentId"]=json.dumps(commentId)
      except (ValueError, AttributeError):
        args["commentId"]=commentId
    data = EmptyClass()
    data.args = args
    data.method = "removeComment"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def removeGroup(self, groupId):
    args = collections.OrderedDict()
    if isinstance(groupId,GetShopBaseClass): 
      args["groupId"]=json.dumps(groupId.__dict__)
    else:
      try:
        args["groupId"]=json.dumps(groupId)
      except (ValueError, AttributeError):
        args["groupId"]=groupId
    data = EmptyClass()
    data.args = args
    data.method = "removeGroup"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def requestAdminRight(self, managerName, managerFunction, applicationInstanceId):
    args = collections.OrderedDict()
    if isinstance(managerName,GetShopBaseClass): 
      args["managerName"]=json.dumps(managerName.__dict__)
    else:
      try:
        args["managerName"]=json.dumps(managerName)
      except (ValueError, AttributeError):
        args["managerName"]=managerName
    if isinstance(managerFunction,GetShopBaseClass): 
      args["managerFunction"]=json.dumps(managerFunction.__dict__)
    else:
      try:
        args["managerFunction"]=json.dumps(managerFunction)
      except (ValueError, AttributeError):
        args["managerFunction"]=managerFunction
    if isinstance(applicationInstanceId,GetShopBaseClass): 
      args["applicationInstanceId"]=json.dumps(applicationInstanceId.__dict__)
    else:
      try:
        args["applicationInstanceId"]=json.dumps(applicationInstanceId)
      except (ValueError, AttributeError):
        args["applicationInstanceId"]=applicationInstanceId
    data = EmptyClass()
    data.args = args
    data.method = "requestAdminRight"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def requestNewPincode(self, username, password):
    args = collections.OrderedDict()
    if isinstance(username,GetShopBaseClass): 
      args["username"]=json.dumps(username.__dict__)
    else:
      try:
        args["username"]=json.dumps(username)
      except (ValueError, AttributeError):
        args["username"]=username
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "requestNewPincode"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def resetPassword(self, resetCode, username, newPassword):
    args = collections.OrderedDict()
    if isinstance(resetCode,GetShopBaseClass): 
      args["resetCode"]=json.dumps(resetCode.__dict__)
    else:
      try:
        args["resetCode"]=json.dumps(resetCode)
      except (ValueError, AttributeError):
        args["resetCode"]=resetCode
    if isinstance(username,GetShopBaseClass): 
      args["username"]=json.dumps(username.__dict__)
    else:
      try:
        args["username"]=json.dumps(username)
      except (ValueError, AttributeError):
        args["username"]=username
    if isinstance(newPassword,GetShopBaseClass): 
      args["newPassword"]=json.dumps(newPassword.__dict__)
    else:
      try:
        args["newPassword"]=json.dumps(newPassword)
      except (ValueError, AttributeError):
        args["newPassword"]=newPassword
    data = EmptyClass()
    data.args = args
    data.method = "resetPassword"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def saveGroup(self, group):
    args = collections.OrderedDict()
    if isinstance(group,GetShopBaseClass): 
      args["group"]=json.dumps(group.__dict__)
    else:
      try:
        args["group"]=json.dumps(group)
      except (ValueError, AttributeError):
        args["group"]=group
    data = EmptyClass()
    data.args = args
    data.method = "saveGroup"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def saveUser(self, user):
    args = collections.OrderedDict()
    if isinstance(user,GetShopBaseClass): 
      args["user"]=json.dumps(user.__dict__)
    else:
      try:
        args["user"]=json.dumps(user)
      except (ValueError, AttributeError):
        args["user"]=user
    data = EmptyClass()
    data.args = args
    data.method = "saveUser"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def sendResetCode(self, title, text, username):
    args = collections.OrderedDict()
    if isinstance(title,GetShopBaseClass): 
      args["title"]=json.dumps(title.__dict__)
    else:
      try:
        args["title"]=json.dumps(title)
      except (ValueError, AttributeError):
        args["title"]=title
    if isinstance(text,GetShopBaseClass): 
      args["text"]=json.dumps(text.__dict__)
    else:
      try:
        args["text"]=json.dumps(text)
      except (ValueError, AttributeError):
        args["text"]=text
    if isinstance(username,GetShopBaseClass): 
      args["username"]=json.dumps(username.__dict__)
    else:
      try:
        args["username"]=json.dumps(username)
      except (ValueError, AttributeError):
        args["username"]=username
    data = EmptyClass()
    data.args = args
    data.method = "sendResetCode"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def updatePassword(self, userId, oldPassword, newPassword):
    args = collections.OrderedDict()
    if isinstance(userId,GetShopBaseClass): 
      args["userId"]=json.dumps(userId.__dict__)
    else:
      try:
        args["userId"]=json.dumps(userId)
      except (ValueError, AttributeError):
        args["userId"]=userId
    if isinstance(oldPassword,GetShopBaseClass): 
      args["oldPassword"]=json.dumps(oldPassword.__dict__)
    else:
      try:
        args["oldPassword"]=json.dumps(oldPassword)
      except (ValueError, AttributeError):
        args["oldPassword"]=oldPassword
    if isinstance(newPassword,GetShopBaseClass): 
      args["newPassword"]=json.dumps(newPassword.__dict__)
    else:
      try:
        args["newPassword"]=json.dumps(newPassword)
      except (ValueError, AttributeError):
        args["newPassword"]=newPassword
    data = EmptyClass()
    data.args = args
    data.method = "updatePassword"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

  def upgradeUserToGetShopAdmin(self, password):
    args = collections.OrderedDict()
    if isinstance(password,GetShopBaseClass): 
      args["password"]=json.dumps(password.__dict__)
    else:
      try:
        args["password"]=json.dumps(password)
      except (ValueError, AttributeError):
        args["password"]=password
    data = EmptyClass()
    data.args = args
    data.method = "upgradeUserToGetShopAdmin"
    data.interfaceName = "core.usermanager.IUserManager"
    return self.communicationHelper.sendMessage(data)

class UtilManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def getBase64EncodedPDFWebPage(self, urlToPage):
    args = collections.OrderedDict()
    if isinstance(urlToPage,GetShopBaseClass): 
      args["urlToPage"]=json.dumps(urlToPage.__dict__)
    else:
      try:
        args["urlToPage"]=json.dumps(urlToPage)
      except (ValueError, AttributeError):
        args["urlToPage"]=urlToPage
    data = EmptyClass()
    data.args = args
    data.method = "getBase64EncodedPDFWebPage"
    data.interfaceName = "core.utils.IUtilManager"
    return self.communicationHelper.sendMessage(data)

  def getCompaniesFromBrReg(self, search):
    args = collections.OrderedDict()
    if isinstance(search,GetShopBaseClass): 
      args["search"]=json.dumps(search.__dict__)
    else:
      try:
        args["search"]=json.dumps(search)
      except (ValueError, AttributeError):
        args["search"]=search
    data = EmptyClass()
    data.args = args
    data.method = "getCompaniesFromBrReg"
    data.interfaceName = "core.utils.IUtilManager"
    return self.communicationHelper.sendMessage(data)

  def getCompanyFromBrReg(self, companyVatNumber):
    args = collections.OrderedDict()
    if isinstance(companyVatNumber,GetShopBaseClass): 
      args["companyVatNumber"]=json.dumps(companyVatNumber.__dict__)
    else:
      try:
        args["companyVatNumber"]=json.dumps(companyVatNumber)
      except (ValueError, AttributeError):
        args["companyVatNumber"]=companyVatNumber
    data = EmptyClass()
    data.args = args
    data.method = "getCompanyFromBrReg"
    data.interfaceName = "core.utils.IUtilManager"
    return self.communicationHelper.sendMessage(data)

  def getFile(self, id):
    args = collections.OrderedDict()
    if isinstance(id,GetShopBaseClass): 
      args["id"]=json.dumps(id.__dict__)
    else:
      try:
        args["id"]=json.dumps(id)
      except (ValueError, AttributeError):
        args["id"]=id
    data = EmptyClass()
    data.args = args
    data.method = "getFile"
    data.interfaceName = "core.utils.IUtilManager"
    return self.communicationHelper.sendMessage(data)

  def saveFile(self, file):
    args = collections.OrderedDict()
    if isinstance(file,GetShopBaseClass): 
      args["file"]=json.dumps(file.__dict__)
    else:
      try:
        args["file"]=json.dumps(file)
      except (ValueError, AttributeError):
        args["file"]=file
    data = EmptyClass()
    data.args = args
    data.method = "saveFile"
    data.interfaceName = "core.utils.IUtilManager"
    return self.communicationHelper.sendMessage(data)

class YouTubeManager(object):
  def __init__(self, communicationHelper):
    self.communicationHelper = communicationHelper
  def searchYoutube(self, searchword):
    args = collections.OrderedDict()
    if isinstance(searchword,GetShopBaseClass): 
      args["searchword"]=json.dumps(searchword.__dict__)
    else:
      try:
        args["searchword"]=json.dumps(searchword)
      except (ValueError, AttributeError):
        args["searchword"]=searchword
    data = EmptyClass()
    data.args = args
    data.method = "searchYoutube"
    data.interfaceName = "core.youtubemanager.IYouTubeManager"
    return self.communicationHelper.sendMessage(data)

class GetShopApi(object):
  def __init__(self, address):
    self.communicationHelper = CommunicationHelper(address)
    self.ArxManager = ArxManager(self.communicationHelper)
    self.BannerManager = BannerManager(self.communicationHelper)
    self.BigStock = BigStock(self.communicationHelper)
    self.BrainTreeManager = BrainTreeManager(self.communicationHelper)
    self.CalendarManager = CalendarManager(self.communicationHelper)
    self.CarTuningManager = CarTuningManager(self.communicationHelper)
    self.CartManager = CartManager(self.communicationHelper)
    self.ChatManager = ChatManager(self.communicationHelper)
    self.ContentManager = ContentManager(self.communicationHelper)
    self.FooterManager = FooterManager(self.communicationHelper)
    self.GalleryManager = GalleryManager(self.communicationHelper)
    self.GetShop = GetShop(self.communicationHelper)
    self.GetShopApplicationPool = GetShopApplicationPool(self.communicationHelper)
    self.HotelBookingManager = HotelBookingManager(self.communicationHelper)
    self.InformationScreenManager = InformationScreenManager(self.communicationHelper)
    self.InvoiceManager = InvoiceManager(self.communicationHelper)
    self.LasGruppenPDFGenerator = LasGruppenPDFGenerator(self.communicationHelper)
    self.ListManager = ListManager(self.communicationHelper)
    self.LogoManager = LogoManager(self.communicationHelper)
    self.MecaApi = MecaApi(self.communicationHelper)
    self.MessageManager = MessageManager(self.communicationHelper)
    self.MobileManager = MobileManager(self.communicationHelper)
    self.NewsLetterManager = NewsLetterManager(self.communicationHelper)
    self.NewsManager = NewsManager(self.communicationHelper)
    self.OrderManager = OrderManager(self.communicationHelper)
    self.PageManager = PageManager(self.communicationHelper)
    self.PkkControlManager = PkkControlManager(self.communicationHelper)
    self.ProductManager = ProductManager(self.communicationHelper)
    self.ReportingManager = ReportingManager(self.communicationHelper)
    self.SedoxProductManager = SedoxProductManager(self.communicationHelper)
    self.StoreApplicationInstancePool = StoreApplicationInstancePool(self.communicationHelper)
    self.StoreApplicationPool = StoreApplicationPool(self.communicationHelper)
    self.StoreManager = StoreManager(self.communicationHelper)
    self.UserManager = UserManager(self.communicationHelper)
    self.UtilManager = UtilManager(self.communicationHelper)
    self.YouTubeManager = YouTubeManager(self.communicationHelper)
    self.StoreManager.initializeStore(address, self.communicationHelper.sessionId)
