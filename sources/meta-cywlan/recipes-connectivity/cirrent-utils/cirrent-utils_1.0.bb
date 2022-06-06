SUMMARY = "Cirrent Utilities"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${WORKDIR}/cypress-cirrent/etc/cirrent/LICENSE;md5=2d1367e6350241c58382708c462efb61"

inherit systemd

INSANE_SKIP_${PN} = "already-stripped build-deps"

SRC_URI = " file://%F_CIRRENT_FILE% "

DEPENDS = "libnl openssl"

do_install() {
	install -d ${D}/etc/cirrent
	install -d ${D}/etc/cirrent/certs
	install -d ${D}/etc/cirrent/scripts
	install -d ${D}/etc/default
	install -d ${D}/etc/init.d
	install -d ${D}/usr/bin
	install -d ${D}/usr/lib
	install -d ${D}/usr/lib/cirrent

	install -m 0644 ${WORKDIR}/cypress-cirrent/etc/cirrent/ca_timers.conf ${D}/etc/cirrent/
	install -m 0644 ${WORKDIR}/cypress-cirrent/etc/cirrent/certs/ca-certificates.crt ${D}/etc/cirrent/certs/
	install -m 0644 ${WORKDIR}/cypress-cirrent/etc/cirrent/cirrent_agent.conf ${D}/etc/cirrent/
	install -m 0644 ${WORKDIR}/cypress-cirrent/etc/cirrent/scripts/* ${D}/etc/cirrent/scripts/
	install -m 0644 ${WORKDIR}/cypress-cirrent/etc/cirrent/LICENSE ${D}/etc/cirrent/
	install -m 0644 ${WORKDIR}/cypress-cirrent/etc/cirrent/README ${D}/etc/cirrent/
	install -m 0755 ${WORKDIR}/cypress-cirrent/etc/default/cirrent ${D}/etc/default
	install -m 0755 ${WORKDIR}/cypress-cirrent/etc/init.d/cirrent ${D}/etc/init.d

	install -m 0755 ${WORKDIR}/cypress-cirrent/usr/bin/* ${D}/usr/bin/

	install -m 0444 ${WORKDIR}/cypress-cirrent/usr/lib/libcirrent_api.so ${D}/usr/lib/
	install -m 0444 ${WORKDIR}/cypress-cirrent/usr/lib/libcirrent_cred.so ${D}/usr/lib/
	install -m 0444 ${WORKDIR}/cypress-cirrent/usr/lib/cirrent/libcurl.so.4.5.0 ${D}/usr/lib/cirrent/

	ln -sf libcurl.so.4.5.0 ${D}/usr/lib/cirrent/libcurl.so.4
}

PACKAGES = "${PN}"
FILES_${PN} += " \
	/usr/lib/*.so \
	/usr/lib/cirrent/* \
	/usr/bin/* \
	/etc/cirrent/* \
	/etc/cirrent/certs/* \
	/etc/cirrent/scripts/* \
	/etc/default/* \
	/etc/init.d/cirrent \
"
