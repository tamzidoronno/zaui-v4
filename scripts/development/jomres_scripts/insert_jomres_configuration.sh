storeId='687cd85e-4812-405a-9532-7748acc29d13';
host='localhost:27018'
cmfClientTokenUrl="https://jomres.zauistay.com/jomres/api/"
clientBaseUrl="https://jomres.zauistay.com/jomres/api/"
cmfRestApiClientId="xUirogUaOFQFOQF"
cmfRestApiClientSecret="HdZDhHbHPxGXjNhZsQSLDDvbrcgLgaDXfGhfyawUJNbbLALtxs"
channelName="test_channel"
mongo --port=27018 --eval "const storeId='$storeId', host='$host', cmfClientTokenUrl='$cmfClientTokenUrl',
  clientBaseUrl='$clientBaseUrl' , cmfRestApiClientId='$cmfRestApiClientId', cmfRestApiClientSecret='$cmfRestApiClientSecret',
   channelName='$channelName'" insertion_jomres_configuration_helper.js