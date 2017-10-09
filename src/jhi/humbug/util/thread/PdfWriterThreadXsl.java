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

package jhi.humbug.util.thread;

import org.apache.commons.io.FileUtils;
import org.apache.fop.apps.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import org.eclipse.swt.*;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.nio.file.Path;
import java.util.*;

import javax.imageio.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;

import jhi.humbug.gui.*;
import jhi.humbug.gui.i18n.*;
import jhi.humbug.gui.widget.*;
import jhi.humbug.util.*;
import jhi.swtcommons.util.*;

/**
 * @author Sebastian Raubach
 */
public class PdfWriterThreadXsl implements IRunnableWithProgress
{
	private Collection<BarcodeRow> barcodes;
	private File                   file;

	/**
	 * Writes the given {@link Collection} of {@link BarcodeRow}s to the {@link File}
	 *
	 * @param barcodes The {@link Collection} of {@link BarcodeRow}s to export
	 * @param file     The {@link File} to write the barcodes to
	 */
	public PdfWriterThreadXsl(Collection<BarcodeRow> barcodes, File file)
	{
		this.barcodes = barcodes;
		this.file = file;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
	{
		/* Ensure there is a monitor of some sort */
		if (monitor == null)
			monitor = new NullProgressMonitor();

        /* Tell the user what you are doing */
		monitor.beginTask(RB.getString(RB.THREAD_EXPORT_TITLE), IProgressMonitor.UNKNOWN);

		try
		{
			/* Get the margin and padding settings */
			int marginLeft = Margin.INSTANCE.getMargin(SWT.LEFT);
			int marginTop = Margin.INSTANCE.getMargin(SWT.TOP);
			int marginRight = Margin.INSTANCE.getMargin(SWT.RIGHT);
			int marginBottom = Margin.INSTANCE.getMargin(SWT.BOTTOM);
			int barcodePadding = Integer.parseInt(HumbugParameterStore.INSTANCE.getAsString(HumbugParameter.barcodePadding));
			int maxImageHeight = Integer.parseInt(HumbugParameterStore.INSTANCE.getAsString(HumbugParameter.imageHeight));

			TemplateFileWriter.TemplatePlaceholders p = new TemplateFileWriter.TemplatePlaceholders(barcodePadding, marginTop, marginRight, marginBottom, marginLeft, maxImageHeight);

			/* Write the xsl file to a temporary location */
			Path directory = Files.createTempDirectory("humbug");
			File xsl = new File(directory.toFile(), "pdf.xsl");

			System.out.println(directory.toFile().getAbsolutePath());

			InputStream stream;
			if (Humbug.WITHIN_JAR)
				stream = PdfWriterThreadXsl.class.getResourceAsStream("/barcode-template2.xsl");
			else
				stream = new FileInputStream(new File("res", "/barcode-template2.xsl"));

			try (BufferedReader br = new BufferedReader(new InputStreamReader(stream));
				 BufferedWriter bw = new BufferedWriter(new FileWriter(xsl)))
			{
				TemplateFileWriter.INSTANCE.write(br, bw, p);
			}

			BarcodeCollection collection = new BarcodeCollection();

			for (BarcodeRow barcode : barcodes)
			{
				Path image = Files.createTempFile(directory, "barcode", ".png");
				ImageIO.write(barcode.getBufferedImage(), "png", image.toFile());
				BarcodeCollection.BarcodeItem item = collection.add(barcode);
				item.setBarcode(image.toFile());
			}

			File xml = new File(directory.toFile(), "xml.xml");
			Serializer serializer = new Persister();
			serializer.write(collection, xml);

			convertToPDF(xsl, xml, file);

			/* Delete the temporary folder */
			FileUtils.deleteDirectory(directory.toFile());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			DialogUtils.showError(e.getLocalizedMessage());
		}

		DialogUtils.showInformation(RB.getString(RB.INFORMATION_SAVE, file.getAbsolutePath()));
	}

	private void convertToPDF(File xsltFile, File xmlFile, File pdfFile) throws IOException, FOPException, TransformerException
	{
		// the XSL FO file
		// the XML file which provides the input
		StreamSource xmlSource = new StreamSource(xmlFile);
		// create an instance of fop factory
		FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		// a user agent is needed for transformation
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		// Setup output
		try (OutputStream out = new FileOutputStream(pdfFile))
		{
			// Construct fop with desired output format
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

			// Setup XSLT
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

			// Resulting SAX events (the generated FO) must be piped through to FOP
			javax.xml.transform.Result res = new SAXResult(fop.getDefaultHandler());

			// Start XSLT transformation and FOP processing
			// That's where the XML is first transformed to XSL-FO and then
			// PDF is created
			transformer.transform(xmlSource, res);
		}
	}
}