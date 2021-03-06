SUMMARY = "Cypress Binaries"
LICENSE = "BSD"

LIC_FILES_CHKSUM = "file://${WORKDIR}/firmware/LICENCE;md5=cbc5f665d04f741f1e006d2096236ba7"

SRC_URI = " \
	file://%F_FW_FILE% \
	file://nvram.zip \
	file://wlarm_le \
	file://wlarm64_le \
	file://10-network.rules \
	file://%F_BT_FW_FILE% \
"

SRCREV_default = "${AUTOREV}"

S = "${WORKDIR}"
B = "${WORKDIR}"
DEPENDS = " libnl"



do_compile () {
	echo "Compiling: "
        echo "Testing Make        Display:: ${MAKE}"
        echo "Testing bindir      Display:: ${bindir}"
        echo "Testing base_libdir Display:: ${base_libdir}"
        echo "Testing sysconfdir  Display:: ${sysconfdir}"
        echo "Testing S  Display:: ${S}"
        echo "Testing B  Display:: ${B}"
        echo "Testing D  Display:: ${D}"
        echo "WORK_DIR :: ${WORKDIR}"
        echo "PWD :: "
        pwd
}

PACKAGES_prepend = "cypress-binaries-wlarm "
FILES_cypress-binaries-wlarm = "${bindir}/wlarm"

DO_INSTALL_64BIT_BINARIES = "no"
DO_INSTALL_64BIT_BINARIES_mx6 = "no"
DO_INSTALL_64BIT_BINARIES_mx7 = "no"
DO_INSTALL_64BIT_BINARIES_mx8 = "yes"

do_install () {
	echo "Installing: "
	install -d ${D}/lib/firmware/cypress
	install -d ${D}/lib/firmware/cypress/murata-nvram
	install -d ${D}/usr/sbin
	install -d ${D}/etc/udev/rules.d
	install -d ${D}/etc/firmware

	# copy all files (*.bin, *.clm_blob, LICENSE) to /lib/firmware/cypress folder
	install -m 444 ${S}/firmware/* ${D}/lib/firmware/cypress

	# Copying NVRAM files (*.txt) to lib/firmware/cypress and lib/firmware/cypress/murata-nvram
	install -m 444 ${S}/nvram/cyw-fmac-nvram/*.txt ${D}/lib/firmware/cypress/murata-nvram
	install -m 444 ${S}/nvram/cyw-fmac-nvram/brcmfmac4339-sdio.ZP.txt ${D}/lib/firmware/cypress/cyfmac4339-sdio.txt
	install -m 444 ${S}/nvram/cyw-fmac-nvram/brcmfmac4354-sdio.1BB.txt ${D}/lib/firmware/cypress/cyfmac4354-sdio.txt
	install -m 444 ${S}/nvram/cyw-fmac-nvram/brcmfmac4356-pcie.1CX.txt ${D}/lib/firmware/cypress/cyfmac4356-pcie.txt
	install -m 444 ${S}/nvram/cyw-fmac-nvram/brcmfmac43012-sdio.1LV.txt ${D}/lib/firmware/cypress/cyfmac43012-sdio.txt
	install -m 444 ${S}/nvram/cyw-fmac-nvram/brcmfmac43340-sdio.1BW.txt ${D}/lib/firmware/cypress/cyfmac43340-sdio.txt
	install -m 444 ${S}/nvram/cyw-fmac-nvram/brcmfmac43362-sdio.SN8000.txt ${D}/lib/firmware/cypress/cyfmac43362-sdio.txt
	install -m 444 ${S}/nvram/cyw-fmac-nvram/brcmfmac43430-sdio.1DX.txt ${D}/lib/firmware/cypress/cyfmac43430-sdio.txt
	install -m 444 ${S}/nvram/cyw-fmac-nvram/brcmfmac43455-sdio.1MW.txt ${D}/lib/firmware/cypress/cyfmac43455-sdio.txt
	install -m 444 ${S}/nvram/cyw-fmac-nvram/README_NVRAM ${D}/lib/firmware/cypress
	install -m 444 ${S}/nvram/cyfmac54591-pcie.txt ${D}/lib/firmware/cypress/

	install -m 444 ${S}/10-network.rules                  ${D}${sysconfdir}/udev/rules.d/10-network.rules

	# Copying wl tool binary to /usr/sbin
	if [ ${DO_INSTALL_64BIT_BINARIES} = "yes" ]; then
		install -m 755 ${S}/wlarm64_le ${D}/usr/sbin/wl
	else
		install -m 755 ${S}/wlarm_le ${D}/usr/sbin/wl
	fi

	# Copying *.HCD files to etc/firmware
	install -m 444 ${S}/bt-firmware/BCM4345C0_003.001.025.0144.0266.1MW.hcd ${D}${sysconfdir}/firmware/BCM4345C0_003.001.025.0144.0266.1MW.hcd
	install -m 444 ${S}/bt-firmware/BCM43012C0_003.001.015.0102.0141.1LV.hcd ${D}${sysconfdir}/firmware/BCM43012C0_003.001.015.0102.0141.1LV.hcd
	install -m 444 ${S}/bt-firmware/BCM4343A1_001.002.009.0093.0395.1DX.hcd ${D}${sysconfdir}/firmware/BCM43430A1_001.002.009.0093.0395.1DX.hcd
	install -m 444 ${S}/bt-firmware/CYW4350C0.1BB.hcd ${D}${sysconfdir}/firmware/BCM4350C0.1BB.hcd
	install -m 444 ${S}/bt-firmware/BCM4356A2_001.003.015.0106.0403.1CX.hcd ${D}${sysconfdir}/firmware/BCM4354A2_001.003.015.0106.0403.1CX.hcd
}

PACKAGES =+ "${PN}-mfgtest"

FILES_${PN} += "/lib/firmware"
FILES_${PN} += "/lib/firmware/*"
FILES_${PN} += "${bindir}"
FILES_${PN} += "${sbindir}"
FILES_${PN} += "{sysconfdir}/firmware"
FILES_${PN} += "/lib"


FILES_${PN}-mfgtest = " \
	/usr/bin/wl \
"

INSANE_SKIP_${PN} += "build-deps"
INSANE_SKIP_${PN} += "file-rdeps"
