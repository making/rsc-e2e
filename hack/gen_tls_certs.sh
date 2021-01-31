#!/bin/bash

set -ex

ROOT_DOMAIN=$1
SYS_DOMAIN=sys.$ROOT_DOMAIN
APPS_DOMAIN=apps.$ROOT_DOMAIN

SSL_FILE=sslconf-${ROOT_DOMAIN}.conf

#Generate SSL Config with SANs
if [ ! -f $SSL_FILE ]; then
cat > $SSL_FILE <<EOF
[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
[req_distinguished_name]
countryName_default = JA
stateOrProvinceName_default = Tokyo
localityName_default = SF
organizationalUnitName_default = IKAM
[ v3_req ]
# Extensions to add to a certificate request
basicConstraints = CA:FALSE
keyUsage = nonRepudiation, digitalSignature, keyEncipherment
subjectAltName = @alt_names
[alt_names]
DNS.1 = *.${ROOT_DOMAIN}
DNS.2 = *.${SYS_DOMAIN}
DNS.3 = *.${APPS_DOMAIN}
EOF
fi

openssl genrsa -out ${ROOT_DOMAIN}.key 2048
openssl req -new -out ${ROOT_DOMAIN}.csr -subj "/CN=*.${ROOT_DOMAIN}/O=IKAM/C=JA" -key ${ROOT_DOMAIN}.key -config ${SSL_FILE}
openssl req -text -noout -in ${ROOT_DOMAIN}.csr
openssl x509 -req -days 3650 -in ${ROOT_DOMAIN}.csr -signkey ${ROOT_DOMAIN}.key -out ${ROOT_DOMAIN}.crt -extensions v3_req -extfile ${SSL_FILE}
openssl x509 -in ${ROOT_DOMAIN}.crt -text -noout
rm ${ROOT_DOMAIN}.csr ${SSL_FILE}
