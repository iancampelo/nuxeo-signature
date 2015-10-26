package org.nuxeo.ecm.platform.signature.core.sign;

import java.awt.Color;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.signature.api.sign.SignatureAppearanceFactory;
import org.nuxeo.ecm.platform.signature.api.sign.SignatureLayout;
import org.nuxeo.ecm.platform.signature.api.sign.SignatureService;
import org.nuxeo.runtime.api.Framework;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfSignatureAppearance;

public class DefaultSignatureAppearanceFactory implements SignatureAppearanceFactory {

    protected static final Log LOGGER = LogFactory.getLog(DefaultSignatureAppearanceFactory.class);

    @Override
    public void format(PdfSignatureAppearance pdfSignatureAppearance, DocumentModel doc, String principal, String reason) {
        pdfSignatureAppearance.setReason(reason);
        pdfSignatureAppearance.setAcro6Layers(true);
        pdfSignatureAppearance.setRender(PdfSignatureAppearance.SignatureRenderDescription);
        SignatureService service = Framework.getService(SignatureService.class);
        SignatureLayout layout = ((SignatureServiceImpl)service).getSignatureLayout();
        Font layer2Font = FontFactory.getFont(FontFactory.TIMES, layout.getTextSize(), Font.NORMAL, new Color(
                        0x00, 0x00, 0x00));
        pdfSignatureAppearance.setLayer2Font(layer2Font);
    }

}
