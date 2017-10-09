/*
 * Copyright 2017 Sebastian Raubach and Paul Shaw from the
 * Information and Computational Sciences Group at JHI Dundee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jhi.humbug.gui.dialog

import jhi.humbug.gui.Humbug
import jhi.humbug.gui.i18n.RB
import jhi.humbug.gui.widget.Hyperlink
import jhi.humbug.util.FileUtils
import jhi.humbug.util.Resources
import jhi.swtcommons.gui.dialog.I18nDialog
import jhi.swtcommons.gui.layout.GridDataUtils
import jhi.swtcommons.gui.layout.GridLayoutUtils
import jhi.swtcommons.gui.viewer.LicenseFileComboViewer
import jhi.swtcommons.util.Install4jUtils
import jhi.swtcommons.util.OSUtils
import jhi.swtcommons.util.ShellUtils
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.*
import java.util.*

/**
 * @author Sebastian Raubach
 */
class AboutDialog(shell: Shell) : I18nDialog(shell)
{
    private lateinit var tabFolder: TabFolder
    private lateinit var license: Text

    override fun createButtonBar(parent: Composite): Control?
    {
        /* We don't want a button bar, so just return null */
        return null
    }

    override fun configureShell(shell: Shell)
    {
        super.configureShell(shell)
        shell.text = RB.getString(RB.DIALOG_ABOUT_TITLE)
    }

    override fun getInitialLocation(initialSize: Point): Point
    {
        /* Center the dialog based on the parent */
        return ShellUtils.getLocationCenteredTo(parentShell, initialSize)
    }

    override fun createDialogArea(parent: Composite): Control
    {
        val composite = super.createDialogArea(parent) as Composite

        /* Add the app name */
        val name = Label(composite, SWT.WRAP)
        name.text = RB.getString(RB.APPLICATION_TITLE) + " (" + Install4jUtils.getVersion(Humbug::class.java) + ")"

        Resources.Fonts.applyFontSize(name, 16)

        tabFolder = TabFolder(composite, SWT.NONE)

        createAboutPart(tabFolder)
        createLicensePart(tabFolder)

        GridLayoutUtils.useDefault().applyTo(composite)
        GridLayoutUtils.useDefault().applyTo(tabFolder)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(composite)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).widthHint(400 * Resources.getZoomFactor() / 100).applyTo(tabFolder)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.CENTER_TOP).applyTo(name)

        return composite
    }

    override fun create()
    {
        super.create()

        if (OSUtils.isMac())
        {
            tabFolder.setSelection(1)
            tabFolder.setSelection(0)
        }
    }

    /**
     * Creates the [TabItem] containing the license

     * @param parent The parent [TabFolder]
     */
    private fun createLicensePart(parent: TabFolder)
    {
        val item = TabItem(parent, SWT.NONE)
        item.text = RB.getString(RB.DIALOG_ABOUT_TAB_LICENSE)

        val composite = Composite(parent, SWT.NONE)

        val licenseFileComboViewer = object : LicenseFileComboViewer(composite, SWT.NONE)
        {
            override fun getLicenseData(): LinkedHashMap<String, String>
            {
                return linkedMapOf("Humbug" to "LICENSE",
                        "Apache Commons Imaging" to "licences/commons-imaging.txt",
                        "Apache Commons IO" to "licences/commons-io.txt",
                        "Apache Commons Logging" to "licences/commons-logging.txt",
                        "Apache Excalibur" to "licences/excalibur.txt",
                        "Apache FOP" to "licences/fop.txt",
                        "Apache Xalan" to "licences/xalan.txt",
                        "Apache Xerces" to "licences/xerces.txt",
                        "Apache XML Commons" to "licences/xml-apis.txt",
                        "Apache XML Graphics Commons" to "licences/xml-graphics.txt",
                        "Faenza Icon Set" to "licences/faenza.txt",
                        "Simple Xml Serialization" to "licences/simple-xml.txt",
                        "Thumbnailator" to "licences/thumbnailator.txt",
                        "ZXing" to "licences/zxing.txt")
            }

            override fun showLicense(text: String, path: String)
            {
                try
                {
                    license.text = FileUtils.readLicense(path)
                }
                catch (e: Exception)
                {
                    license.text = RB.getString(RB.ERROR_ABOUT_LICENSE)
                    e.printStackTrace()
                }

            }
        }

        license = Text(composite, SWT.WRAP or SWT.BORDER or SWT.V_SCROLL or SWT.READ_ONLY)
        license.background = shell.display.getSystemColor(SWT.COLOR_WHITE)

        licenseFileComboViewer.init()

        /* Do some layout magic here */
        GridLayoutUtils.useDefault().applyTo(composite)

        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(composite)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_TOP).applyTo(licenseFileComboViewer.combo)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).heightHint(1).applyTo(license)

        item.control = composite
    }

    /**
     * Creates the [TabItem] containing the about text

     * @param parent The parent [TabFolder]
     */
    private fun createAboutPart(parent: TabFolder)
    {
        val item = TabItem(parent, SWT.NONE)
        item.text = RB.getString(RB.DIALOG_ABOUT_TAB_ABOUT)

        val composite = Composite(parent, SWT.NONE)

        /* Add the description */
        val desc = Label(composite, SWT.WRAP)
        desc.text = RB.getString(RB.DIALOG_ABOUT_DESCRIPTION)

        /* Add some space */
        Label(composite, SWT.NONE)

        /* Add additional text */
        val additional = Label(composite, SWT.WRAP)
        additional.text = RB.getString(RB.DIALOG_ABOUT_ADDITIONAL)

        /* Add some space */
        Label(composite, SWT.NONE)

        /* Add the copyright information */
        val copyright = Label(composite, SWT.WRAP)
        copyright.text = RB.getString(RB.DIALOG_ABOUT_COPYRIGHT, Calendar.getInstance().get(Calendar.YEAR))

        val linkWrapper = Composite(composite, SWT.NONE)

        /* Add the link to the website */
        val website = Hyperlink(linkWrapper, SWT.WRAP)
        website.setText(RB.getString(RB.DIALOG_ABOUT_WEBSITE_TITLE))
        website.setToolTipText(RB.getString(RB.DIALOG_ABOUT_WEBSITE_URL))
        website.setImage(Resources.Images.WEB)

        /* Add the email address */
        val email = Hyperlink(linkWrapper, SWT.WRAP)
        email.setText(RB.getString(RB.DIALOG_ABOUT_EMAIL_TITLE))
        email.setToolTipText(RB.getString(RB.DIALOG_ABOUT_EMAIL_URL))
        email.setImage(Resources.Images.EMAIL)
        email.setIsEmail(true)

        /* Add the twitter link */
        val twitter = Hyperlink(linkWrapper, SWT.NONE)
        twitter.setText(RB.getString(RB.DIALOG_ABOUT_TWITTER_TITLE))
        twitter.setToolTipText(RB.getString(RB.DIALOG_ABOUT_TWITTER_URL))
        twitter.setImage(Resources.Images.TWITTER_LOGO)

        GridLayoutUtils.useDefault().applyTo(linkWrapper)

        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_TOP).applyTo(desc)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_TOP).applyTo(additional)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.CENTER_TOP).applyTo(copyright)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.CENTER_TOP).applyTo(linkWrapper)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_CENTER).applyTo(email.control)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_CENTER).applyTo(website.control)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_CENTER).applyTo(twitter.control)

        Label(composite, SWT.NONE)

        val jhi = Hyperlink(composite, SWT.NONE)
        jhi.setImage(Resources.Images.JHI_LOGO)
        jhi.setToolTipText(RB.getString(RB.DIALOG_ABOUT_JHI_URL))

        /* Do some layout magic here */
        GridLayoutUtils.useDefault().marginBottom(15).marginWidth(15).applyTo(composite)

        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(composite)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.CENTER_BOTH_FALSE).applyTo(jhi.control)

        item.control = composite
    }
}
