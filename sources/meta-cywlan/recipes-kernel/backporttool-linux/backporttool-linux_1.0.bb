SUMMARY = "Cypress FMAC backports"
DESCRIPTION = "Cypress FMAC backports"

LICENSE = "GPLv2"

inherit linux-kernel-base kernel-arch module-base

#DEPENDS += " linux-imx"
DEPENDS += " backporttool-native"

S = "${WORKDIR}/backporttool-linux-${PV}"
B = "${WORKDIR}/backporttool-linux-${PV}/"

#You should set variable CROSS_COMPILE, not a CROSS-COMPILE
export CROSS_COMPILE = "${TARGET_PREFIX}"

do_compile() {
	# Linux kernel build system is expected to do the right thing

	echo "TEST_CROSS_COMPILE:: ${CROSS_COMPILE}"
	echo "TEST_TARGET_PREFIX:: ${TARGET_PREFIX}"
	echo "TEST_ARCH:: ${ARCH}"
	echo "TEST_TARGET_ARCH:: ${TARGET_ARCH}"
	echo "STAGING_KERNEL_BUILDDIR: ${STAGING_KERNEL_BUILDDIR}"
	echo "STAGING_KERNEL_DIR: ${STAGING_KERNEL_DIR}"
	echo "S DIR:  ${S}"
	echo "B DIR:: ${B}"
	echo "KERNEL_VERSION:: ${KERNEL_VERSION}"

#	rm -rf .git
#	cp -a ${TMPDIR}/work/imx6sxsabresd-linux/backporttool-native/${PV}-r0/backporttool-native-${PV}/. .
    cp -a ${TMPDIR}/work/imx6qdlsabresd-fslc-linux-gnueabi/backporttool-native/${PV}-r0/backporttool-native-${PV}/. .
	oe_runmake KLIB="${STAGING_KERNEL_DIR}" KLIB_BUILD="${STAGING_KERNEL_BUILDDIR}" modules
}

do_install() {
	echo "D DIR:: ${D}"
	echo "PN:: ${PN}"
	install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/broadcom/brcm80211/brcmfmac
	install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/broadcom/brcm80211/brcmutil
	install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/compat
	install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/net/wireless

	install -m 644 ${S}/drivers/net/wireless/broadcom/brcm80211/brcmfmac/brcmfmac.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/broadcom/brcm80211/brcmfmac/brcmfmac.ko
	install -m 644 ${S}/drivers/net/wireless/broadcom/brcm80211/brcmutil/brcmutil.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/broadcom/brcm80211/brcmutil/brcmutil.ko
	install -m 644 ${S}/compat/compat.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/compat/compat.ko
	install -m 644 ${S}/net/wireless/cfg80211.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/net/wireless/cfg80211.ko
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES_${PN} += " \
	/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/broadcom/brcm80211/brcmfmac/brcmfmac.ko \
	/lib/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/broadcom/brcm80211/brcmutil/brcmutil.ko \
	/lib/modules/${KERNEL_VERSION}/kernel/compat/compat.ko \
	/lib/modules/${KERNEL_VERSION}/kernel/net/wireless/cfg80211.ko \
"

PACKAGES += "FILES-${PN}"
RPROVIDES_${PN} += " \
	kernel-module-brcmfmac-${KERNEL_VERSION} \
	kernel-module-brcmutil-${KERNEL_VERSION} \
	kernel-module-compat-${KERNEL_VERSION} \
	kernel-module-cfg80211-${KERNEL_VERSION}"


