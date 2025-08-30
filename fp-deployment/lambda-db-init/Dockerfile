FROM amazon/aws-lambda-python:3.13-x86_64

RUN microdnf update -y && \
    microdnf install -y \
        p7zip \
        p7zip-plugins && \
    microdnf clean all

WORKDIR /deploy

ENTRYPOINT ["bash"]