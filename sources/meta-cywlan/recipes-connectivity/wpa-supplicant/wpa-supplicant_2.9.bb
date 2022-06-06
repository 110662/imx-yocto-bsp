SUMMARY = "Client for Wi-Fi Protected Access (WPA)"
HOMEPAGE = "http://w1.fi/wpa_supplicant/"
BUGTRACKER = "http://w1.fi/security/"
SECTION = "network"
LICENSE = "BSD"
LIC_FILES_CHKSUM = " \
					file://${WORKDIR}/cypress-hostap_${PV}/COPYING;md5=279b4f5abb9c153c285221855ddb78cc \
					file://${B}/wpa_supplicant.c;beginline=1;endline=12;md5=0a8b56d3543498b742b9c0e94cc2d18b "

DEPENDS = "dbus libnl openssl"
RRECOMMENDS_${PN} = "wpa-supplicant-passphrase wpa-supplicant-cli"

PACKAGECONFIG ??= "gnutls"
PACKAGECONFIG[gnutls] = ",,gnutls libgcrypt"
PACKAGECONFIG[openssl] = ",,openssl"

inherit pkgconfig systemd

SYSTEMD_SERVICE_${PN} = "wpa_supplicant.service wpa_supplicant-nl80211@.service wpa_supplicant-wired@.service"
SYSTEMD_AUTO_ENABLE = "disable"

PV = "2_9"

SRC_URI = " \
           git://w1.fi/hostap.git;protocol=http;tag=hostap_${PV} \
           file://config_supplicant \
           file://wpa-supplicant.sh \
           file://wpa_supplicant.conf \
           file://wpa_supplicant.conf-sane \
           file://99_wpa_supplicant \
           file://%F_HOSTAP_FILE% \
"

CVE_PRODUCT = "wpa_supplicant"

S = "${WORKDIR}/hostap_${PV}"
B = "${WORKDIR}/hostap_${PV}/wpa_supplicant"

PACKAGES_prepend = "wpa-supplicant-passphrase wpa-supplicant-cli "
FILES_wpa-supplicant-passphrase = "${bindir}/wpa_passphrase"
FILES_wpa-supplicant-cli = "${sbindir}/wpa_cli"
FILES_${PN} += "${datadir}/dbus-1/system-services/*"
CONFFILES_${PN} += "${sysconfdir}/wpa_supplicant.conf"

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

do_configure () {
	${MAKE} -C ${B} clean
	install -m 0755 ${WORKDIR}/config_supplicant ${B}/.config
	echo "CFLAGS +=\"-I${STAGING_INCDIR}/libnl3\"" >> ${B}/.config
	echo "DRV_CFLAGS +=\"-I${STAGING_INCDIR}/libnl3\"" >> ${B}/.config

	if echo "${PACKAGECONFIG}" | grep -qw "openssl"; then
		ssl=openssl
	elif echo "${PACKAGECONFIG}" | grep -qw "gnutls"; then
		ssl=gnutls
	fi
	if [ -n "$ssl" ]; then
		sed -i "s/%ssl%/$ssl/" ${B}/.config
	fi

	# For rebuild
	rm -f ${B}/*.d ${B}/dbus/*.d
}

export EXTRA_CFLAGS = "${CFLAGS}"
export BINDIR = "${sbindir}"

do_compile () {
	echo "Compiling: "
	echo "ARCH: ${ARCH} "
	echo "CROSS_COMPILE: ${CROSS_COMPILE} "
	unset CFLAGS CPPFLAGS CXXFLAGS
	sed -e "s:CFLAGS\ =.*:& \$(EXTRA_CFLAGS):g" -i ${S}/src/lib.rules

	oe_runmake -C ${B}
}

do_install () {
	install -d ${D}${sbindir}
	install -m 755 ${B}/wpa_supplicant ${D}${sbindir}
	install -m 755 ${B}/wpa_cli        ${D}${sbindir}

	install -d ${D}${bindir}
	install -m 755 ${B}/wpa_passphrase ${D}${bindir}

	install -d ${D}${docdir}/wpa_supplicant
	install -m 644 ${B}/README ${WORKDIR}/wpa_supplicant.conf ${D}${docdir}/wpa_supplicant

	install -d ${D}${sysconfdir}
	install -m 600 ${WORKDIR}/wpa_supplicant.conf-sane ${D}${sysconfdir}/wpa_supplicant.conf

	install -d ${D}${sysconfdir}/network/if-pre-up.d/
	install -d ${D}${sysconfdir}/network/if-post-down.d/
	install -d ${D}${sysconfdir}/network/if-down.d/
	install -m 755 ${WORKDIR}/wpa-supplicant.sh ${D}${sysconfdir}/network/if-pre-up.d/wpa-supplicant
	cd ${D}${sysconfdir}/network/ && \
	ln -sf ../if-pre-up.d/wpa-supplicant if-post-down.d/wpa-supplicant

	install -d ${D}/${sysconfdir}/dbus-1/system.d
	install -m 644 ${S}/wpa_supplicant/dbus/dbus-wpa_supplicant.conf ${D}/${sysconfdir}/dbus-1/system.d
	install -d ${D}/${datadir}/dbus-1/system-services
	install -m 644 ${S}/wpa_supplicant/dbus/*.service ${D}/${datadir}/dbus-1/system-services

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}/${systemd_unitdir}/system
		install -m 644 ${S}/wpa_supplicant/systemd/*.service ${D}/${systemd_unitdir}/system
	fi

	install -d ${D}/etc/default/volatiles
	install -m 0644 ${WORKDIR}/99_wpa_supplicant ${D}/etc/default/volatiles
}

pkg_postinst_wpa-supplicant () {
	# If we're offline, we don't need to do this.
	if [ "x$D" = "x" ]; then
		killall -q -HUP dbus-daemon || true
	fi

}
