package org.jcodec.samples.transcode;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.common.Codec;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.Format;
import org.jcodec.common.MuxerTrack;
import org.jcodec.common.VideoEncoder;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture8Bit;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mkv.muxer.MKVMuxer;

/**
 * A profile to transcode into AVC (H.264) muxed into MKV. 
 * @author Stanislav Vitvitskiy
 */
class Png2mkv extends FromImgProfile {
    private MKVMuxer muxer;

    @Override
    public Set<Format> outputFormat() {
        return TranscodeMain.formats(Format.MKV);
    }

    @Override
    public Set<Codec> outputVideoCodec() {
        return TranscodeMain.codecs(Codec.H264);
    }

    @Override
    protected VideoEncoder getEncoder() {
        return H264Encoder.createH264Encoder();
    }

    @Override
    protected MuxerTrack getMuxerTrack(SeekableByteChannel sink, DemuxerTrackMeta inTrackMeta, Picture8Bit yuv,
            Packet firstPacket) throws IOException {
        muxer = new MKVMuxer(sink);
        return muxer.createVideoTrack(new Size(yuv.getWidth(), yuv.getHeight()), "V_MPEG4/ISO/AVC");
    }

    @Override
    protected void finalizeMuxer() throws IOException {
        muxer.mux();
    }

    @Override
    protected Packet encodeFrame(VideoEncoder encoder, Picture8Bit yuv, Packet inPacket, ByteBuffer buf) {
        return Packet.createPacketWithData(inPacket, encoder.encodeFrame8Bit(yuv, buf));
    }
}