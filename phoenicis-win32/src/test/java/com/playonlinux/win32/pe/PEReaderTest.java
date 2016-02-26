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

package com.playonlinux.win32.pe;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PEReaderTest {
    private PEFile peFile;

    @Before
    public void setUp() throws IOException {
        peFile = PEReader.parseExecutable(this.getClass().getResourceAsStream("winecfg.exe"));
    }

    @Test
    public void testMagicNumber() {
        assertEquals(0x5A4D, peFile.imageDOSHeader.eMagic.getWord());
    }

    @Test
    public void testPeHeaderSignature() {
        assertEquals(0x4550, peFile.imageNTHeaders.signature.getContent());
    }

    @Test
    public void testMachine() {
        assertEquals(0x14C, peFile.imageNTHeaders.fileHeader.machine.getWord());
    }

    @Test
    public void testNumberOfSections() {
        assertEquals(3, peFile.imageNTHeaders.fileHeader.numberOfSections.getWord());
    }

    @Test
    public void testNumberOfSymbols() {
        assertEquals(0, peFile.imageNTHeaders.fileHeader.numberOfSymbols.getContent());
    }

    @Test
    public void testSizeOfOptionalHeader() {
        assertEquals(224, peFile.imageNTHeaders.fileHeader.sizeOfOptionalHeader.getWord());
    }

    @Test
    public void testCharacteristics() {
        assertEquals(0x102, peFile.imageNTHeaders.fileHeader.characteristics.getWord());
    }

    @Test
    public void testOptionalHeaderMagic() {
        assertEquals(0x10B, peFile.imageNTHeaders.optionalHeader.magic.getWord());
    }

    @Test
    public void testOptionalHeaderLinkerVersion() {
        assertEquals(0, peFile.imageNTHeaders.optionalHeader.minorLinkerVersion.intValue());
        assertEquals(0, peFile.imageNTHeaders.optionalHeader.minorLinkerVersion.intValue());
    }

    @Test
    public void testOptionalHeaderSizeOfCode() {
        assertEquals(8, peFile.imageNTHeaders.optionalHeader.sizeOfCode.getContent());
    }

    @Test
    public void testOptionalHeaderSizeOfInitializedData() {
        assertEquals(0, peFile.imageNTHeaders.optionalHeader.sizeOfInitializedData.getContent());
    }

    @Test
    public void testOptionalHeaderSizeOfUninitializedData() {
        assertEquals(0, peFile.imageNTHeaders.optionalHeader.sizeOfUninitializedData.getContent());
    }

    @Test
    public void testOptionalHeaderBaseOfCode() {
        assertEquals(0x1000, peFile.imageNTHeaders.optionalHeader.baseOfCode.getContent());
    }

    @Test
    public void testOptionalHeaderImageBase() {
        assertEquals(0x10000000, peFile.imageNTHeaders.optionalHeader.imageBase.getUnsignedValue());
    }

    @Test
    public void testOptionalHeaderBaseOfData() {
        assertEquals(0, peFile.imageNTHeaders.optionalHeader.baseOfData.getContent());
    }

    @Test
    public void testOptionalHeaderSizeOfHeaders() {
        assertEquals(512, peFile.imageNTHeaders.optionalHeader.sizeOfHeaders.getUnsignedValue());
    }

    @Test
    public void testOptionalHeaderSubsystem() {
        assertEquals(2, peFile.imageNTHeaders.optionalHeader.subsystem.getUnsignedValue());
    }

    @Test
    public void testOptionalHeaderDllCharacteristics() {
        assertEquals(0x100, peFile.imageNTHeaders.optionalHeader.dllCharacteristics.getWord());
    }

    @Test
    public void testNumberOfDataDirectoryInOptionalHeader() {
        assertEquals(16, peFile.imageNTHeaders.optionalHeader.dataDirectory.length);
    }

    @Test
    public void testRealModeStubProgramSize() {
        assertEquals(96, peFile.imageDOSHeader.eLfanew.intValue());
    }

    @Test
    public void testSectionHeaderNames() {
        Assert.assertEquals(".text\u0000\u0000\u0000", new String(peFile.sectionHeaders[0].name));
        Assert.assertEquals(".reloc\u0000\u0000", new String(peFile.sectionHeaders[1].name));
        Assert.assertEquals(".rsrc\u0000\u0000\u0000", new String(peFile.sectionHeaders[2].name));
    }

    @Test
    public void testRsrcSizeOfRawData() {
        assertEquals(0xB8038, peFile.sectionHeaders[2].sizeOfRawData.getContent());
    }

    @Test
    public void testRsrcVirtualAddress() {
        assertEquals(0x00003000, peFile.sectionHeaders[2].virtualAddress.getContent());
    }

    @Test
    public void testVirtuaSize() {
        assertEquals(0x000B9000, peFile.sectionHeaders[2].physicalAddressOrVirtualSize.getContent());
    }

    @Test
    public void testCharacteristic() {
        assertEquals(0x40000040, peFile.sectionHeaders[2].characteristics.getContent());
    }

    @Test
    public void testResourceSection() {
        assertEquals(1, peFile.resourceSection.imageResourceDirectory.numberOfNamedEntries.getUnsignedValue());
        assertEquals(4, peFile.resourceSection.imageResourceDirectory.numberOfIdEntries.getUnsignedValue());
    }

    @Test
    public void testRealModeStubProgram() {
        assertArrayEquals(java.util.Arrays.copyOf("Wine placeholder DLL".getBytes(), 32), peFile.realModeStubProgram);
    }
}