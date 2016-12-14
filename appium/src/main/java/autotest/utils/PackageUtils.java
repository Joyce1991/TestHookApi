package autotest.utils;

import android.content.res.AXmlResourceParser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by wallace on 16/11/14.
 */

public class PackageUtils {
    private static final float[] RADIX_MULTS = new float[]{0.00390625F, 3.051758E-5F, 1.192093E-7F, 4.656613E-10F};
    private static final String[] DIMENSION_UNITS = new String[]{"px", "dip", "sp", "pt", "in", "mm", "", ""};
    private static final String[] FRACTION_UNITS = new String[]{"%", "%p", "", "", "", "", "", ""};
    private String appVersion;
    private String appVersionCode;
    private String packageName;
    private String launchActivity;

    public PackageUtils(File apkFile) throws IOException, DocumentException {
        if (!apkFile.exists()) {
            return;
        }

        ZipFile zip = new ZipFile(apkFile);
        ZipEntry manifestEntry = zip.getEntry("AndroidManifest.xml");
        InputStream in = null;

        try {
            in = zip.getInputStream(manifestEntry);
            String content = decomplierManifest(in);

            SAXReader saxReader = new SAXReader();
            Document apkDocument = saxReader.read(new StringReader(content));
            Element apkManifestElement = apkDocument.getRootElement();

            this.packageName = apkManifestElement.attribute("package").getValue();
            this.appVersion = apkManifestElement.attribute("versionName").getValue();
            this.appVersionCode = apkManifestElement.attribute("versionCode").getValue();

            List<Element> categoryLaunchers = apkManifestElement.selectNodes("//category[@android:name='android.intent.category.LAUNCHER']");
            for (int j = 0; j < categoryLaunchers.size(); j++) {
                Element activityByCategory = categoryLaunchers.get(j).getParent().getParent();
                this.launchActivity = activityByCategory.attributeValue("name");
                break;
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private static String decomplierManifest(InputStream stream) {
        StringBuffer buffer = new StringBuffer();
        if (stream == null) {
            buffer.append("Usage: AXMLPrinter <binary xml file>");
            return buffer.toString();
        }
        try {
            try {
                AXmlResourceParser e = new AXmlResourceParser();
                e.open(stream);
                StringBuilder indent = new StringBuilder(10);

                while(true) {
                    while(true) {
                        int type = e.next();
                        if(type == 1) {
                            return buffer.toString();
                        }

                        switch(type) {
                            case 0:
                                buffer.append(String.format("<?xml version=\"1.0\" encoding=\"utf-8\"?>", new Object[0]));
                            case 1:
                            default:
                                break;
                            case 2:
                                buffer.append(String.format("%s<%s%s", new Object[]{indent, getNamespacePrefix(e.getPrefix()), e.getName()}));
                                indent.append("\t");
                                int namespaceCountBefore = e.getNamespaceCount(e.getDepth() - 1);
                                int namespaceCount = e.getNamespaceCount(e.getDepth());

                                int i;
                                for(i = namespaceCountBefore; i != namespaceCount; ++i) {
                                    buffer.append(String.format("%sxmlns:%s=\"%s\"", new Object[]{indent, e.getNamespacePrefix(i), e.getNamespaceUri(i)}));
                                }

                                for(i = 0; i != e.getAttributeCount(); ++i) {
                                    buffer.append(String.format("%s%s%s=\"%s\"", new Object[]{indent, getNamespacePrefix(e.getAttributePrefix(i)), e.getAttributeName(i), getAttributeValue(e, i)}));
                                }

                                buffer.append(String.format("%s>", new Object[]{indent}));
                                break;
                            case 3:
                                indent.setLength(indent.length() - "\t".length());
                                buffer.append(String.format("%s</%s%s>", new Object[]{indent, getNamespacePrefix(e.getPrefix()), e.getName()}));
                                break;
                            case 4:
                                buffer.append(String.format("%s%s", new Object[]{indent, e.getText()}));
                        }
                    }
                }
            } catch (Exception var8) {
                var8.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }

    private static String getNamespacePrefix(String prefix) {
        return prefix != null && prefix.length() != 0?prefix + ":":"";
    }

    private static String getAttributeValue(AXmlResourceParser parser, int index) {
        int type = parser.getAttributeValueType(index);
        int data = parser.getAttributeValueData(index);
        return type == 3?parser.getAttributeValue(index):(type == 2?String.format("?%s%08X", new Object[]{getPackage(data), Integer.valueOf(data)}):(type == 1?String.format("@%s%08X", new Object[]{getPackage(data), Integer.valueOf(data)}):(type == 4?String.valueOf(Float.intBitsToFloat(data)):(type == 17?String.format("0x%08X", new Object[]{Integer.valueOf(data)}):(type == 18?(data != 0?"true":"false"):(type == 5?Float.toString(complexToFloat(data)) + DIMENSION_UNITS[data & 15]:(type == 6?Float.toString(complexToFloat(data)) + FRACTION_UNITS[data & 15]:(type >= 28 && type <= 31?String.format("#%08X", new Object[]{Integer.valueOf(data)}):(type >= 16 && type <= 31?String.valueOf(data):String.format("<0x%X, type 0x%02X>", new Object[]{Integer.valueOf(data), Integer.valueOf(type)}))))))))));
    }

    private static String getPackage(int id) {
        return id >>> 24 == 1?"android:":"";
    }

    private static void log(String format, Object... arguments) {
        System.out.printf(format, arguments);
        System.out.println();
    }

    public static float complexToFloat(int complex) {
        return (float)(complex & -256) * RADIX_MULTS[complex >> 4 & 3];
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public String getAppVersionCode() {
        return this.appVersionCode;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getLaunchActivity() {
        return this.launchActivity;
    }
}
