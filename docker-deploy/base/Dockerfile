#FROM debian:11
FROM debian:bullseye-slim

#RUN mv /etc/apt/sources.list /etc/apt/sources.list.bkp
#COPY base/sources.list /etc/apt/sources.list

WORKDIR /root

RUN apt-get -q update && apt-get -q install -y --no-install-recommends \
    iputils-ping openssh-client openssh-server rsync grsync curl gnupg sudo vim netcat net-tools \
    libaio1 libncurses5 libnuma1 zlib1g libssl-dev libgcc1 libstdc++6 libtinfo5 perl expect \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# passwordless with ssh
RUN ssh-keygen -q -t rsa -N '' -f ~/.ssh/id_rsa \
    && cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

COPY base/entrypoint.sh /dockerentry/entrypoint.sh
RUN chmod +x /dockerentry/entrypoint.sh
RUN ls -l /dockerentry/entrypoint.sh

ENTRYPOINT ["/bin/bash", "/dockerentry/entrypoint.sh"]
