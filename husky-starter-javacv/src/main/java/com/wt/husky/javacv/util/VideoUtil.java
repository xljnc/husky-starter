package com.wt.husky.javacv.util;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;

import java.io.*;
import java.util.concurrent.*;

/**
 * 视屏工具类
 *
 * @author wutian2@myhexin.com
 * @date 2022/6/7
 */
@Slf4j
public class VideoUtil {

    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

    private static final long MAX_VIDEO_TIME = 5 * 60 * 1000l;

    public static void main(String[] args) {
        File input = new File("D:/video/animal.wmv");
        File output = new File("D:/video/animal-part.wmv");
        try {
            if (!output.exists())
                output.createNewFile();
            FFmpegLogCallback.set();
            split(0, 10, new FileInputStream(input), new FileOutputStream(output));
        } catch (Exception e) {
            log.error("视屏截取失败", e);
        }
    }

    //记得关闭视频流
    public static boolean split(long startTime, long endTime, InputStream is, OutputStream os) {
        if (startTime < 0 || endTime <= startTime || (endTime - startTime) > MAX_VIDEO_TIME)
            throw new RuntimeException("参数异常");
        try {
            return CompletableFuture.supplyAsync(() -> handleVideo(startTime, endTime, is, os)
                    , pool).exceptionally(e -> {
                log.error("视屏截取失败", e);
                return false;
            }).get();
        } catch (Exception e) {
            log.error("视屏截取失败", e);
            if (e instanceof InterruptedException)
                Thread.currentThread().interrupt();
            return false;
        }
    }

    private static boolean handleVideo(long startTime, long endTime, InputStream is, OutputStream os) {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(is);
        FFmpegFrameRecorder recorder = null;
        try {
            grabber.start();
            grabber.setTimestamp(endTime);
            AVPacket endPacket = grabber.grabPacket();
            if (endPacket == null)
                return false;
            grabber.setTimestamp(startTime);
            recorder = new FFmpegFrameRecorder(os, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setFormat(grabber.getFormat());
            recorder.setAudioCodec(grabber.getAudioCodec());
            recorder.setVideoCodec(grabber.getVideoCodec());
//            recorder.start(grabber.getFormatContext());
            recorder.start();
            AVPacket packet;
            while ((packet = grabber.grabPacket()) != null) {
                recorder.recordPacket(packet);
                if (endPacket.equals(packet))
                    break;
            }
            return true;
        } catch (Exception e) {
            log.error("视屏截取失败", e);
            return false;
        } finally {
            try {
                grabber.stop();
                if (recorder != null)
                    recorder.stop();
            } catch (FFmpegFrameGrabber.Exception e) {
                log.error("视屏抓取器关闭失败", e);
            } catch (FFmpegFrameRecorder.Exception e) {
                log.error("视屏记录器关闭失败", e);
            }
        }
    }
}
