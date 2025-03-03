package com.sh.fbs.commom.utils;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class LZ4Utils {
    private static final LZ4Factory factory = LZ4Factory.fastestInstance();
    private static final LZ4Compressor compressor = factory.fastCompressor();
    private static final LZ4FastDecompressor decompressor = factory.fastDecompressor();

    /**
     * 压缩字节数组
     * @param data 待压缩的字节数组
     * @return 压缩后的字节数组
     * @throws IOException 压缩过程中可能出现的IO异常
     */
    public static byte[] compress(byte[] data) throws IOException {
        int maxCompressedLength = compressor.maxCompressedLength(data.length);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(data, 0, data.length, compressed, 0);

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedLength + 4)) {
            bos.write(intToBytes(data.length));
            bos.write(compressed, 0, compressedLength);
            return bos.toByteArray();
        }
    }

    /**
     * 解压缩字节数组
     * @param compressedData 压缩后的字节数组
     * @return 解压缩后的字节数组
     * @throws IOException 解压缩过程中可能出现的IO异常
     */
    public static byte[] decompress(byte[] compressedData) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData)) {
            byte[] lengthBytes = new byte[4];
            bis.read(lengthBytes);
            int decompressedLength = bytesToInt(lengthBytes);

            byte[] compressed = new byte[compressedData.length - 4];
            bis.read(compressed);

            byte[] decompressed = new byte[decompressedLength];
            decompressor.decompress(compressed, 0, decompressed, 0, decompressedLength);
            return decompressed;
        }
    }
    /**
     * 压缩字符串
     * @param data 待压缩的字符串
     * @return 压缩后的字符串（Base64 编码）
     * @throws IOException 压缩过程中可能出现的 IO 异常
     */
    public static String compress(String data) throws IOException {
        byte[] originalBytes = data.getBytes();
        int maxCompressedLength = compressor.maxCompressedLength(originalBytes.length);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(originalBytes, 0, originalBytes.length, compressed, 0);

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedLength + 4)) {
            bos.write(intToBytes(originalBytes.length));
            bos.write(compressed, 0, compressedLength);
            byte[] resultBytes = bos.toByteArray();
            return Base64.getEncoder().encodeToString(resultBytes);
        }
    }

    /**
     * 解压缩字符串
     * @param compressedData 压缩后的字符串（Base64 编码）
     * @return 解压缩后的字符串
     * @throws IOException 解压缩过程中可能出现的 IO 异常
     */
    public static String decompress(String compressedData) throws IOException {
        byte[] compressedBytes = Base64.getDecoder().decode(compressedData);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedBytes)) {
            byte[] lengthBytes = new byte[4];
            bis.read(lengthBytes);
            int decompressedLength = bytesToInt(lengthBytes);

            byte[] compressed = new byte[compressedBytes.length - 4];
            bis.read(compressed);

            byte[] decompressed = new byte[decompressedLength];
            decompressor.decompress(compressed, 0, decompressed, 0, decompressedLength);
            return new String(decompressed);
        }
    }

    /**
     * 将整数转换为字节数组
     * @param value 整数
     * @return 字节数组
     */
    private static byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    /**
     * 将字节数组转换为整数
     * @param bytes 字节数组
     * @return 整数
     */
    private static int bytesToInt(byte[] bytes) {
        return (bytes[0] & 0xFF) << 24 |
                (bytes[1] & 0xFF) << 16 |
                (bytes[2] & 0xFF) << 8 |
                (bytes[3] & 0xFF);
    }
}
