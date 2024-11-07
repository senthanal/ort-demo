FROM ghcr.io/oss-review-toolkit/ort-minimal:38.0.0

WORKDIR $HOME
COPY .ort/config $HOME/.ort/config
COPY entrypoint.sh $HOME/entrypoint.sh
COPY .npmrc $HOME/.npmrc
COPY package.json $HOME/package.json
COPY package-lock.json $HOME/package-lock.json
COPY .ort.yml $HOME/.ort.yml

ENTRYPOINT ["/home/ort/entrypoint.sh"]
