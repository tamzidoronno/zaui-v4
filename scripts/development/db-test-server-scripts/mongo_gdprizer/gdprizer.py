import argparse
import csv
import random
from os.path import abspath, join, dirname
from random import randint
from copy import deepcopy
import re
import progressbar
import pymongo
import names_generator

class Disguiser:
    def __init__(self, port, host):
        self.updated_users = {} # dictionary of * fullName: string_for_update *
        self.updated_companies = {} # dictionary of * fullName: string_for_update *
        self.port = port
        self.host = host

    def run(self):
        db_connection = connect_to_db(self.host, self.port)
        usermanager_database = db_connection["UserManager"]
        ordermanager_database = db_connection["OrderManager"]

        for collection_name in usermanager_database.list_collection_names():
            print ("Processing Usermanager->" + collection_name)
            pbar = progressbar.ProgressBar(max_value=usermanager_database[collection_name].count_documents({}))
            for document in pbar(usermanager_database[collection_name].find()):
                self.disguise_document(usermanager_database[collection_name], document)

        for collection_name in ordermanager_database.list_collection_names():
            print ("Processing OrderManager->" + collection_name)
            pbar = progressbar.ProgressBar(max_value=ordermanager_database[collection_name].count_documents({}))
            for document in pbar(ordermanager_database[collection_name].find()):
                self.disguise_document(ordermanager_database[collection_name], document)



    def disguise_document(self, collection, document):
        if document["className"] == "com.thundashop.core.usermanager.data.Company":
            self.disguise_company_document(collection, document)
        elif document["className"] == "com.thundashop.core.usermanager.data.User":
            self.disguise_user_document(collection, document)
        elif document["className"] == "com.thundashop.core.ordermanager.data.Order":
            self.disguise_order_document(collection, document)

    def disguise_user_document(self, collection, document):
        #elements_to_update = ['fullName', 'emailAddress', 'password',  'cellPhone', 'address'['phone', 'emailAddress', 'fullName', 'address', 'city'] ]
        string_for_update = {}
        document_invalid = 'address' not in document or not isinstance(document['address'], dict)
        if document_invalid:
            return

        string_for_update = self.disguise_address(document, string_for_update)
        string_for_update = self.disguise_name(document, 'fullName', string_for_update, '')
        string_for_update = disguise_phone_num( 'cellPhone', string_for_update)
        if ('address' in string_for_update):
            string_for_update['address'] = disguise_phone_num('phone', string_for_update['address'])
        string_for_update = disguise_email( string_for_update)

        string_for_update = self.disguise_repeated_fields_in_subdocument(string_for_update, document, sub_document='address', fields=['fullName', 'emailAddress'])

        if string_for_update != {}:
            self.updated_users[document['fullName']] = string_for_update
            collection.update_one(document, {"$set": string_for_update})

    def disguise_company_document(self, collection, document):
        string_for_update = {}
        string_for_update = self.disguise_name(document, 'name', string_for_update, 'hotel')
        string_for_update = self.disguise_address(document, string_for_update)
        if string_for_update != {}:
            self.updated_companies['fullName'] = string_for_update
            collection.update_one(document, {"$set": string_for_update})

    # todo prettfy
    def disguise_order_document(self, collection, document):
        # document['cart']['address'] : fullName phone emailAddress postCode address city
        if 'cart' not in document or 'address' not in document['cart']:
            return
        string_for_update = {'cart': deepcopy(document['cart'])}
        document_cart_address = document['cart']['address']
        stringforupdate_cart_address = string_for_update['cart']['address']
        fullName = document_cart_address['fullName']
        try: # if there has already been that user updated in UserManager::class:User
            updated_fields_from_user_document = self.updated_users[fullName]
            stringforupdate_cart_address['fullName'] = updated_fields_from_user_document['fullName']
        except KeyError:
            try: # if there has already been that user updated in UserManager::class:Company
                updated_fields_from_user_document = self.updated_companies[fullName]
                stringforupdate_cart_address['fullName'] = updated_fields_from_user_document['fullName']
            except:
                self.updated_users[fullName] = {}
                self.updated_users[fullName] = self.disguise_order_address_for_newuser(fullName, self.updated_users[fullName], document)
                updated_fields_from_user_document = self.updated_users[fullName]
        # print("updating order fullName: " + fullName)
        if exists_and_not_empty('phone', document_cart_address) and exists_and_not_empty('phone', updated_fields_from_user_document['address']):
            stringforupdate_cart_address['phone'] = updated_fields_from_user_document['address']['phone']
        if exists_and_not_empty('emailAddress', document_cart_address) and exists_and_not_empty('emailAddress', updated_fields_from_user_document):
            stringforupdate_cart_address['emailAddress'] = updated_fields_from_user_document['emailAddress']
        if exists_and_not_empty('address', document_cart_address) and exists_and_not_empty('address', updated_fields_from_user_document['address']):
            stringforupdate_cart_address['address'] = updated_fields_from_user_document['address']['address']
        if exists_and_not_empty('city', document_cart_address) and exists_and_not_empty('city', updated_fields_from_user_document['address']):
            stringforupdate_cart_address['city'] = updated_fields_from_user_document['address']['city']
        if exists_and_not_empty('postCode', document_cart_address) and exists_and_not_empty('postCode', updated_fields_from_user_document['address']):
            stringforupdate_cart_address['postCode'] = updated_fields_from_user_document['address']['postCode']
        if exists_and_not_empty('fullName', document_cart_address) and exists_and_not_empty('fullName', updated_fields_from_user_document):
            stringforupdate_cart_address['fullName'] = updated_fields_from_user_document['fullName']
        if string_for_update != {'cart': document['cart'] }:
            collection.update_one(document, {"$set": string_for_update})

    def disguise_order_address_for_newuser(self, fullName, string_for_update, document):
        string_for_update['fullName'] = generate_name('AS')
        string_for_update = self.disguise_address(document['cart'], string_for_update)
        string_for_update = disguise_email(string_for_update)
        string_for_update = disguise_phone_num('phone', string_for_update)
        string_for_update['address']['phone'] = string_for_update['phone']
        return string_for_update

    def disguise_name(self, document, field_to_update, string_for_update, name_extension = ''):
        new_name = generate_name(name_extension)
        if (exists_and_not_empty(field_to_update, document)):
            string_for_update[field_to_update] = new_name

        return string_for_update


    def disguise_address(self, document, string_for_update):
        address_field = "address"
        address_stored_in_subdocument = (address_field in document and isinstance(document[address_field], dict) and address_field in document[address_field])
        address_stored_in_root_document = (address_field in document and not isinstance(document[address_field], dict) )

        sub_fields = ["address", "city", "postCode"]

        if (address_stored_in_subdocument):
            new_address_document = {field: f"{generate_specific_field(field)}" for field in sub_fields}
            string_for_update[address_field] = new_address_document

        elif (address_stored_in_root_document):
            print ("DIDN'T CHANGE document[address_field] " , document[address_field])
            exit(77)
        return string_for_update

    def disguise_repeated_fields_in_subdocument(self, string_for_update, document, sub_document, fields):
        for field in fields:
            if (field in document[sub_document]):
                document_doesnt_have_empty_fields = field in string_for_update
                field_is_not_empty = document[sub_document][field] != ''
                if document_doesnt_have_empty_fields and field_is_not_empty:
                    string_for_update[sub_document][field] = string_for_update[field]
        return string_for_update



def main():
    print("Starting..")
    global args
    parser = argparse.ArgumentParser(description='Export shop data')
    parser.add_argument('--port', action='store', default='27018')
    parser.add_argument('--host', action='store', default='127.0.0.1')
    args = parser.parse_args()

    d = Disguiser(args.port, args.host)
    d.run()
    print (" > > gdprizer.py finished.")

def generate_specific_field(field):
    if field == "postCode":
        return random_with_N_digits(4)
    elif field == "city":
        return random_pokemon() + "vegen"
    elif field == "address":
        return f" Street{random_with_N_digits(3)}"
    else:
        return f"{field} {random_with_N_digits(4)}"


def disguise_phone_num(field_name, string_for_update):
    string_for_update[field_name] = random_with_N_digits(8)
    return string_for_update

def disguise_email(string_for_update):
    string_for_update['emailAddress'] = random_pokemon() + "@testing.com"
    return string_for_update

def generate_name(name_extension):
    formatting_style = "capital"
    if name_extension == "hotel":
        formatting_style = "hyphen" # will generate name in formatting style funny-einstein-hotel

    return names_generator.generate_name(formatting_style)  + name_extension

def random_with_N_digits(n):
    range_start = 10**(n-1)
    range_end = (10**n)-1
    return randint(range_start, range_end)

def exists_and_not_empty(field_name, dictionary):
    return field_name in dictionary and dictionary[field_name] != ''

def random_pokemon():
    full_path = lambda filename: abspath(join(dirname(__file__), filename))
    poke_csv = csv.reader(open(full_path('mocked_input.csv'), 'r'))
    allPokemons = []
    for poke in poke_csv:
        allPokemons = poke
    rand = random.Random()
    random_pokemon = rand.choice(allPokemons)

    return remove_funky_characters(random_pokemon)


def remove_funky_characters(random_pokemon):
    return re.sub('[\W_]+', '', random_pokemon)

# def exists_and_not_empty(field):
#     try:
#         return field != None and field != ''
#     except:
#         return False

def connect_to_db(host, port):
    print(f"Connecting to db {host}:{port}")
    return pymongo.MongoClient(f"mongodb://{host}:{port}/")

if __name__ == "__main__":
    main()
