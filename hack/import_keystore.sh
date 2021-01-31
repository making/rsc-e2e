#!/bin/bash
CERT=$1
KEY=$2

openssl pkcs12 -export \
        -name rsc-e2e \
        -in ${CERT}  \
        -inkey ${KEY} \
        -out ./keystore.p12 \
        -password pass:foobar

keytool -importkeystore \
        -destkeystore ./keystore.jks \
        -srckeystore ./keystore.p12 \
        -deststoretype pkcs12 \
        -srcstoretype pkcs12 \
        -alias rsc-e2e \
        -deststorepass changeme \
        -destkeypass changeme \
        -srcstorepass foobar \
        -srckeypass foobar \
        -noprompt
rm -f ./keystore.p12