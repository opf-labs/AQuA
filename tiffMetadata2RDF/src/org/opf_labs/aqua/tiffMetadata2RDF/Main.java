package org.opf_labs.aqua.tiffMetadata2RDF;

import java.awt.color.ICC_Profile;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.exceptions.FitsException;

public class Main {

	public static final String uriroot = "http://shaw/content/";
	public static final String mdNamespace = "http://shaw/md/";

	private static Stack<File> fileStack = new Stack<File>();
	private static Model model;
	private static Fits fits;

	public static void main(String[] args) throws FitsConfigurationException {
		if (args.length < 1 || args.length > 3) {
			doUsage();
			System.exit(0);
		}
		
		File root = new File(args[0]);
				
		File fitsCache = null;
		if (args.length >= 3 && args[2] != null) {
			fitsCache = new File(args[1]);
		}
		
		File outputFile = null;
		if (args.length >= 2 && args[1] != null) {
			outputFile = new File(args[1]);
		} else {
			outputFile = new File("./tiff2RDF-"+System.currentTimeMillis()+".rdf");
		}
		
		if (!root.exists() || !root.canRead()) {
			doError("Unable to read directory " + root.getAbsolutePath() + ". Please check and try again.");
			System.exit(0);
		}

		System.out.println("Reading directory...");
		populateFileStack(root);

		System.out.println("Found " + fileStack.size() + " files...");

		try {
			String fitsHome = null;
			fitsHome = System.getenv().get("FITS_HOME");
			
			if (fitsHome == null) {
				doError("FITS_HOME not set...");
				System.exit(0);
			}
			
			fits = new Fits(fitsHome);
			model = ModelFactory.createDefaultModel();
			model.setNsPrefix("shawmd", mdNamespace);

			int count = 0;

			while (!fileStack.empty()) {

				count++;

				File cfile = fileStack.pop();
				System.out.println("["+count+"] Processing: " +cfile.getAbsolutePath());

				String resourceUri = uriroot+cfile.getAbsolutePath();

				// Add a resource for this item...
				Resource tiffRdf = model.createResource(resourceUri);

				System.out.println("Running FITS...");
				FitsOutput fo = fits.examine(cfile);
				FitsMetadataElement element = fo.getMetadataElement("iccProfileName");
				if (element != null) {
					tiffRdf.addProperty(model.createProperty(mdNamespace, "iccprofilename"), getResource(element.getValue()));
				}
				
				if (fitsCache != null) {
					System.out.println("Saving FITS output...");
					File fitsFileName = new File(fitsCache.getAbsolutePath()+"/FITS_files/"+cfile.getAbsolutePath());
					fitsFileName.mkdirs();
					fo.saveToDisk(fitsFileName.getAbsolutePath()+"/fits.xml");
				}

				System.out.println("Running Sanselan...");
				IImageMetadata imd = Sanselan.getMetadata(cfile);
				ICC_Profile iccprofile = Sanselan.getICCProfile(cfile);
				ImageInfo imageInfo = Sanselan.getImageInfo(cfile);

				tiffRdf.addProperty(model.createProperty(mdNamespace, "bitsperpixel"), getResource(""+imageInfo.getBitsPerPixel()));
				tiffRdf.addProperty(model.createProperty(mdNamespace, "coltypedesc"), getResource(imageInfo.getColorTypeDescription()));
				tiffRdf.addProperty(model.createProperty(mdNamespace, "formatname"), getResource(imageInfo.getFormatName()));
				
				
				// TODO should probably check all of the above for null values! :-)
				if (iccprofile != null) {
					tiffRdf.addProperty(model.createProperty(mdNamespace, "iccprofile"), getResource(iccprofile.getColorSpaceType()+""));
				}
				
				if (imd instanceof TiffImageMetadata) {
					TiffImageMetadata timd = (TiffImageMetadata) imd;
					List<?> tags = timd.getAllFields();
					for (Object of : tags) {
						TiffField field = null;
						if (of instanceof TiffField) {
							field = (TiffField) of;
							String pname = field.getTagName();
							pname = pname.toLowerCase();
							pname = pname.replace(" ", "");
							pname = pname.replace(":", "");
							tiffRdf.addProperty(model.createProperty(mdNamespace, pname), getResource(field.getValueDescription()));
						} else {
							System.out.println("Some other kind of field!!!!!!!!!!!");
						}
					}
				} else {
					System.out.println("Weird - not a tiffImageMetadata!");
				}
			}

			System.out.println("Saving RDF...");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			model.write(baos);

			try {
				FileUtils.writeStringToFile(outputFile, baos.toString());
			} catch (IOException e) {
				System.out.println("Unable to write metadata file "+e.getMessage());
			}

		} catch (ImageReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FitsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	private static void doError(String string) {
		System.err.println("Sorry, it didn't work!");
		System.err.println(string);
		System.err.println();
	}

	private static void doUsage() {
		System.out.println("tiffMetadata2RDF");
		System.out.println("================");
		System.out.println("Usage: tiff2RDF imageDirectory [outputFile] [fitsCacheDir]");
		System.out.println();
	}

	private static Resource getResource(String value) {
		String resourceName = mdNamespace+norm(value);
		Resource bay = model.getResource(resourceName);
		if (bay.getProperty(DC.title) == null) {
			bay.addProperty(DC.title, value);
		}
		return bay;
	}

	private static void populateFileStack(File current) {
		if (current.isDirectory()) {
			File[] files = current.listFiles();
			for (File f : files) {
				populateFileStack(f);
			}
		} else {
			fileStack.push(current);
		}
	}

	public static String norm(String str) {
		str = str.toLowerCase();
		str = str.replaceAll("\\s", "");

		try {
			str = URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		return str;
	}
}
