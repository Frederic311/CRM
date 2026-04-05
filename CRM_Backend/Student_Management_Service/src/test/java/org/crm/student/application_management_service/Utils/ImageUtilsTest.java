package org.crm.student.application_management_service.Utils;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageUtilsTest {

    @Test
    void compressAndDecompress_shouldReturnOriginalBytes() {
        byte[] source = "sample-image-like-content-1234567890".getBytes(StandardCharsets.UTF_8);

        byte[] compressed = ImageUtils.compressImage(source);
        byte[] decompressed = ImageUtils.decompressImage(compressed);

        assertEquals(true, compressed.length > 0);
        assertArrayEquals(source, decompressed);
    }

    @Test
    void decompressImage_shouldReturnEmptyArrayForInvalidCompressedData() {
        byte[] invalid = new byte[] {1, 2, 3, 4, 5};

        byte[] result = ImageUtils.decompressImage(invalid);

        assertArrayEquals(new byte[0], result);
        assertEquals(0, result.length);
        assertEquals(false, Arrays.equals(invalid, result));
    }
}
