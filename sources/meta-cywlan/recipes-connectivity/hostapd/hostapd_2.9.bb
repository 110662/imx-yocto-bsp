PV = "2_9"
HOMEPAGE = "http://w1.fi/hostapd/"
SECTION = "kernel/userland"
LICENSE = "GPLv2 | BSD"
LIC_FILES_CHKSUM = " \
					file://${WORKDIR}/cypress-hostap_${PV}/COPYING;md5=279b4f5abb9c153c285221855ddb78cc \
					file://${B}/main.c;beginline=1;endline=12;md5=2a1ab6a67b65218f7ba36fbcf45bcec2 "
DEPENDS = "libnl openssl"
SUMMARY = "User space daemon for extended IEEE 802.11 management"

inherit update-rc.d systemd
INITSCRIPT_NAME = "hostapd"

SYSTEMD_SERVICE_${PN} = "hostapd.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

SRC_URI = " \
    git://w1.fi/hostap.git;protocol=http;tag=hostap_${PV} \
    file://config_hostapd \
    file://init \
    file://hostapd.service \
    file://%F_HOSTAP_FILE% \
    file://udhcpd.conf \
"

S = "${WORKDIR}/hostap_${PV}"
B = "${WORKDIR}/hostap_${PV}/hostapd"

addtask local_patch_source before do_patch after do_unpack
do_local_patch_source () {
	mkdir -p ${WORKDIR}/git/patches
	mv ${WORKDIR}/cypress-hostap_${PV}/*.patch ${WORKDIR}/git/patches/
	cd ${WORKDIR}/git/
	git am ${WORKDIR}/git/patches/*.patch

	mv ${WORKDIR}/git/* ${S}/
	cd ${WORKDIR}
	rm -rf git
}

do_configure() {
    install -m 0644 ${WORKDIR}/config_hostapd ${B}/.config
}

do_compile() {
    export CFLAGS="-MMD -O2 -Wall -g -I${STAGING_INCDIR}/libnl3"
    make
}

do_install() {
    install -d ${D}${sbindir} ${D}${sysconfdir}/init.d ${D}${systemd_unitdir}/system/
    install -m 0644 ${B}/hostapd.conf ${D}${sysconfdir}

#   Adding udhcdp.conf
    install -m 0644 ${WORKDIR}/udhcpd.conf ${D}${sysconfdir}

    install -m 0755 ${B}/hostapd ${D}${sbindir}
    install -m 0755 ${B}/hostapd_cli ${D}${sbindir}
    install -m 755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/hostapd
    install -m 0644 ${WORKDIR}/hostapd.service ${D}${systemd_unitdir}/system/
    sed -i -e 's,@SBINDIR@,${sbindir},g' -e 's,@SYSCONFDIR@,${sysconfdir},g' ${D}${systemd_unitdir}/system/hostapd.service
}

CONFFILES_${PN} += "${sysconfdir}/hostapd.conf"
CONFFILES_${PN} += "${sysconfdir}/udhcpd.conf"
