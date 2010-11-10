/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Wojciech Sulejman
 */

package org.nuxeo.ecm.platform.signature.core.sign;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.signature.api.pki.CAService;
import org.nuxeo.ecm.platform.signature.api.sign.SignatureService;
import org.nuxeo.ecm.platform.signature.api.user.CNField;
import org.nuxeo.ecm.platform.signature.api.user.UserInfo;
import org.nuxeo.ecm.platform.signature.core.pki.MockCAServiceImpl;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * @author <a href="mailto:ws@nuxeo.com">Wojciech Sulejman</a>
 *
 */
@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@RepositoryConfig(type = BackendType.H2, user = "Administrator")
@Deploy( { "org.nuxeo.ecm.core", "org.nuxeo.ecm.core.api",
        "org.nuxeo.runtime.management", "org.nuxeo.ecm.platform.signature.core","org.nuxeo.ecm.platform.signature.core.test" })
public class SignatureServiceTest {

    @Inject
    protected CAService cAService;

    @Inject
    protected CoreSession session;

    @Inject
    SignatureService signatureService;

    private final String CA_PASSWORD = "abc";

    private final String USER_PASSWORD = "def";

    private static final Log log = LogFactory.getLog(SignatureServiceTest.class);

    /**
     * mark this true if you want to keep the signed pdf for verification
     * (integration testing scenario)
     */
    private static final boolean KEEP_SIGNED_PDF = true;

    private File origPdfFile;

    private KeyStore userKeystore;
    protected String rootKeystorePath = "test-files/keystore.jks";

    @Before
    public void setup() throws Exception {
        ((MockCAServiceImpl) cAService).setRoot(rootKeystorePath, getPDFCAInfo());
        origPdfFile = FileUtils.getResourceFileFromContext("pdf-tests/original.pdf");
        userKeystore = cAService.initializeUser(getUserInfo(), USER_PASSWORD);
    }

    @AfterClass
    public static void destroy() throws Exception {

    }

    @Test
    public void testSignPDF() throws Exception {
        File outputFile = signatureService.signPDF(userKeystore, getUserInfo(),
                USER_PASSWORD, "test reason", new FileInputStream(origPdfFile));
        assertTrue(outputFile.exists());
        if (KEEP_SIGNED_PDF) {
            log.info("SIGNED PDF: " + outputFile.getAbsolutePath());
        } else {
            outputFile.deleteOnExit();
        }
    }

    public UserInfo getPDFCAInfo() throws Exception {
        Map<CNField, String> userFields;
        userFields = new HashMap<CNField, String>();
        userFields.put(CNField.CN, "PDFCA");
        userFields.put(CNField.C, "US");
        userFields.put(CNField.OU, "CA");
        userFields.put(CNField.O, "Nuxeo");
        userFields.put(CNField.UserID, "PDFCA");
        userFields.put(CNField.Email, "pdfca@nuxeo.com");
        UserInfo userInfo = new UserInfo(userFields);
        return userInfo;
    }

    public UserInfo getUserInfo() throws Exception {
        Map<CNField, String> userFields;
        userFields = new HashMap<CNField, String>();
        userFields.put(CNField.CN, "Wojciech Sulejman");
        userFields.put(CNField.C, "US");
        userFields.put(CNField.OU, "IT");
        userFields.put(CNField.O, "Nuxeo");
        userFields.put(CNField.UserID, "wsulejman");
        userFields.put(CNField.Email, "wsulejman@nuxeo.com");
        UserInfo userInfo = new UserInfo(userFields);
        return userInfo;
    }
}