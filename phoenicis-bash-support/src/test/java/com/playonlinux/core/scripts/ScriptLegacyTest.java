/*
 * Copyright (C) 2015 PÂRIS Quentin
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.playonlinux.core.scripts;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ScriptLegacyTest {

    private final AnyScriptFactory factory = new AnyScriptFactoryImplementation()
	    .withScriptFactory(new ScriptLegacyFactory());

    @Test
    public void testDetectType_passALegacyScript_FormatIsDetected() throws IOException {
	assertEquals(Script.Type.LEGACY, Script.detectScriptType(
		FileUtils.readFileToString(new File(this.getClass().getResource("legacyScriptExample.sh").getPath()))));
    }

    @Test
    public void testDetectType_passALegacyScriptCRLFSeparator_FormatIsDetected() throws IOException {
	assertEquals(Script.Type.LEGACY, Script.detectScriptType(FileUtils
		.readFileToString(new File(this.getClass().getResource("legacyScriptExampleCRLF.sh").getPath()))));
    }

    @Test
    public void testDetectType_passALegacyScriptWithHeader_FormatIsDetected() throws IOException {
	assertEquals(Script.Type.LEGACY, Script.detectScriptType(FileUtils.readFileToString(
		new File(this.getClass().getResource("legacyScriptExampleWithPlayOnLinuxBashHeader.sh").getPath()))));
    }

    @Test
    public void testExtractSignature_bashScriptWithSignature_extracted()
	    throws IOException, ParseException, ScriptFailureException {
	Script legacyScriptWithSignature = factory.createInstanceFromFile(
		new File(this.getClass().getResource("legacyScriptExampleWithSignature.sh").getPath()));
	String expectedSignature = "-----BEGIN PGP SIGNATURE-----\n" + "Version: GnuPG/MacGPG2 v2.0.17 (Darwin)\n"
		+ "\n" + "MOCKED SIGNATURE\n" + "-----END PGP SIGNATURE-----";
	assertEquals(expectedSignature, legacyScriptWithSignature.extractSignature());
    }

    @Test
    public void testExtractContent_bashScriptWithSignature_extracted()
	    throws IOException, ParseException, ScriptFailureException {
	Script legacyScriptWithSignature = factory.createInstanceFromFile(
		new File(this.getClass().getResource("legacyScriptExampleWithSignature.sh").getPath()));
	String expectedSignature = "#!/bin/bash\n" + "[ \"$PLAYONLINUX\" = \"\" ] && exit 0\n"
		+ "source \"$PLAYONLINUX/lib/sources\"\n" + "\n" + "TITLE=\"Legacy script\"\n" + "\n"
		+ "POL_SetupWindow_Init\n" + "POL_SetupWindow_message \"Test\"\n" + "POL_SetupWindow_Close\n" + "\n"
		+ "exit\n";
	assertEquals(expectedSignature, legacyScriptWithSignature.extractContent());
    }

    @Test
    public void testExtractSignature_bashScriptWithSignatureCRLF_extracted()
	    throws IOException, ParseException, ScriptFailureException {
	Script legacyScriptWithSignature = factory.createInstanceFromFile(
		new File(this.getClass().getResource("legacyScriptExampleWithSignatureCRLF.sh").getPath()));
	String expectedSignature = "-----BEGIN PGP SIGNATURE-----\n" + "Version: GnuPG/MacGPG2 v2.0.17 (Darwin)\n"
		+ "\n" + "MOCKED SIGNATURE\n" + "-----END PGP SIGNATURE-----";
	assertEquals(expectedSignature, legacyScriptWithSignature.extractSignature());
    }

    @Test
    public void testExtractContent_bashScriptWithSignatureCRLF_extracted()
	    throws IOException, ParseException, ScriptFailureException {
	Script legacyScriptWithSignature = factory.createInstanceFromFile(
		new File(this.getClass().getResource("legacyScriptExampleWithSignatureCRLF.sh").getPath()));
	String expectedSignature = "#!/bin/bash\n" + "[ \"$PLAYONLINUX\" = \"\" ] && exit 0\n"
		+ "source \"$PLAYONLINUX/lib/sources\"\n" + "\n" + "TITLE=\"Legacy script\"\n" + "\n"
		+ "POL_SetupWindow_Init\n" + "POL_SetupWindow_message \"Test\"\n" + "POL_SetupWindow_Close\n" + "\n"
		+ "exit\n";
	assertEquals(expectedSignature, legacyScriptWithSignature.extractContent());
    }

    @Test(expected = ScriptFailureException.class)
    public void testExtractSignature_bashScriptWithNoSignature_exceptionThrown()
	    throws IOException, ScriptFailureException {
	Script legacyScriptWithoutSignature = factory
		.createInstanceFromFile(new File(this.getClass().getResource("legacyScriptExample.sh").getPath()));
	legacyScriptWithoutSignature.extractSignature();
    }

    @Test
    public void testExtractScript_withRealScript_extracted() throws IOException, ScriptFailureException {
	Script legacyScriptWithSignature = factory
		.createInstanceFromFile(new File(this.getClass().getResource("realScript.sh").getPath()));
	String expectedScript = "#!/bin/bash\n" + "\n" + "[ \"$PLAYONLINUX\" = \"\" ] && exit 0\n"
		+ "source \"$PLAYONLINUX/lib/sources\"\n" + "\n" + "\n" + "PREFIX=\"JediKnightII\"\n"
		+ "TITLE=\"Star wars Jedi Knight II - JediOutcast\"\n" + "EDITOR=\"LucasArts\"\n"
		+ "EDITOR_URL=\"http://www.lucasarts.com\"\n" + "SCRIPTOR=\"Quentin PÂRIS\"\n" + "WINEVERSION=\"1.4\"\n"
		+ "\n" + "POL_SetupWindow_Init\n" + "POL_Debug_Init\n" + "\n" + "#Presentation\n"
		+ "POL_SetupWindow_presentation \"$TITLE\" \"$EDITOR\" \"$EDITOR_URL\" \"$SCRIPTOR\" \"$PREFIX\"\n"
		+ "\n" + "POL_SetupWindow_InstallMethod \"CD,LOCAL\"\n" + "\n" + "if [ \"$POL_SELECTED_FILE\" ]; then\n"
		+ "\tSetupIs=\"$POL_SELECTED_FILE\"\n" + "else\n" + "\tif [ \"$INSTALL_METHOD\" = \"CD\" ]; then\n"
		+ "\t\tPOL_SetupWindow_cdrom\n" + "\t\tPOL_SetupWindow_check_cdrom \"GameData/Setup.exe\"\n"
		+ "\t\tSetupIs=\"$CDROM/GameData/Setup.exe\"\n" + "\tfi\n"
		+ "\tif [ \"$INSTALL_METHOD\" = \"LOCAL\" ]; then\n"
		+ "\t\tPOL_SetupWindow_browse \"$(eval_gettext 'Please select the setup file to run')\" \"$TITLE\"\n"
		+ "\t\tSetupIs=\"$APP_ANSWER\"\n" + "\tfi\n" + "fi\n" + "\n" + "POL_Wine_SelectPrefix \"$PREFIX\"\n"
		+ "POL_Wine_PrefixCreate \"$WINEVERSION\"\n" + "\n" + "POL_Wine_WaitBefore \"$TITLE\"\n"
		+ "[ \"$POL_OS\" = \"Mac\" ] && Set_Managed Off\n" + "POL_Wine \"$SetupIs\"\n" + "\n"
		+ "POL_Shortcut \"JediOutcast.exe\" \"$TITLE\"\n" + "\n" + "POL_SetupWindow_Close\n" + "exit\n";
	assertEquals(expectedScript, legacyScriptWithSignature.extractContent());
    }
}