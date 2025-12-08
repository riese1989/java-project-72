FROM gradle:9.2.1

WORKDIR /app

COPY /app .

RUN gradle installDist

CMD ./build/install/app/bin/app