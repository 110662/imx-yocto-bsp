cp ./sources/meta-cywlan/freescale/imx6sxsabresd.conf ./sources/meta-freescale/conf/machine/

. ./setup-environment $@

echo "INTERNAL_MIRROR = \"http://localhost\"" >> conf/local.conf
echo "CORE_IMAGE_EXTRA_INSTALL += \" hostap-conf hostap-utils hostapd backporttool-linux cypress-binaries iperf3 cirrent-utils net-tools tcpdump openssh e2fsprogs-resize2fs iperf2 sudo procps \"" >> conf/local.conf
echo "IMAGE_ROOTFS_EXTRA_SPACE = \"131072\"" >> conf/local.conf
echo "PACKAGE_EXCLUDE += \" packagegroup-core-ssh-dropbear \"" >> conf/local.conf
echo "BBLAYERS += \" \${BSPDIR}/sources/meta-cywlan \"" >> conf/bblayers.conf
echo ""
echo "CORRECTION: Cypress modified the following files"
echo "  - bblayers.conf present in $BUILD_DIR/conf/"
echo "  - local.conf present in $BUILD_DIR/conf/"
echo "  - imx6sxsabresd.conf present in sources/meta-freescale/conf/machine/"
echo ""
echo "Cypress Wireless LAN setup complete. Create an image with:"
echo "    $ bitbake $IMAGE_NAME"
echo ""
