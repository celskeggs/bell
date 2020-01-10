package com.celskeggs.bell.vm.zip;

public class ZLib {
    // TODO: incorporate this into the sandbox system

    public static final int Z_NO_FLUSH = 0;
    public static final int Z_PARTIAL_FLUSH = 1;
    public static final int Z_SYNC_FLUSH = 2;
    public static final int Z_FULL_FLUSH = 3;
    public static final int Z_FINISH = 4;
    public static final int Z_BLOCK = 5;
    public static final int Z_TREES = 6;
    
    public static final int Z_OK = 0;
    public static final int Z_STREAM_END = 1;
    public static final int Z_NEED_DICT = 2;
    public static final int Z_ERRNO = -1;
    public static final int Z_STREAM_ERROR = -2;
    public static final int Z_DATA_ERROR = -3;
    public static final int Z_MEM_ERROR = -4;
    public static final int Z_BUF_ERROR = -5;
    public static final int Z_VERSION_ERROR = -6;
    
    public static final int Z_NO_COMPRESSION = 0;
    public static final int Z_BEST_SPEED = 1;
    public static final int Z_BEST_COMPRESSION = 9;
    public static final int Z_DEFAULT_COMPRESSION = -1;

    public static final int Z_FILTERED = 1;
    public static final int Z_HUFFMAN_ONLY = 2;
    public static final int Z_RLE = 3;
    public static final int Z_FIXED = 4;
    public static final int Z_DEFAULT_STRATEGY = 0;

    public static final int Z_BINARY = 0;
    public static final int Z_TEXT = 1;
    public static final int Z_ASCII = Z_TEXT;
    public static final int Z_UNKNOWN = 2;
    
    public static final int Z_DEFLATED = 8;
    
    public static final int Z_NULL = 0;
    
    public static final class ZStream {
        public byte[] next_in;
        public int next_in_offset;
        public int avail_in;
        public long total_in;

        public byte[] next_out;
        public int next_out_offset;
        public int avail_out;
        public long total_out;

        public String msg; // error message

        public int data_type;
        public long adler;

        private boolean isInit = false;
        private boolean isDeflate;
        private boolean canReinit = true;

        private void checkError(int error) {
            switch (error) {
            case Z_OK:
                break;
            case Z_MEM_ERROR:
                throw new OutOfMemoryError("zlib OOM: " + this.msg);
            case Z_STREAM_ERROR:
                throw new IllegalArgumentException("zlib stream error: " + this.msg);
            case Z_VERSION_ERROR:
                throw new RuntimeException("incorrect version of zlib: " + this.msg);
            case Z_BUF_ERROR:
                throw new IllegalArgumentException("zlib buffer error: " + this.msg);
            case Z_DATA_ERROR:
                throw new RuntimeException("zlib input error: " + this.msg);
            default:
                throw new RuntimeException("generic zlib error: " + this.msg);
            }
        }

        public synchronized void deflateInit(int level) {
            if (isInit) {
                throw new IllegalStateException("already initialized!");
            }
            if (!canReinit) {
                throw new IllegalStateException("use of deflateInit after finalizer run!");
            }
            int error = ZLib.deflateInit(this, level);
            this.checkError(error);
            isInit = true;
            isDeflate = true;
        }

        public synchronized void deflateInit2(int level, int method, int windowBits, int memLevel, int strategy) {
            if (isInit) {
                throw new IllegalStateException("already initialized!");
            }
            if (!canReinit) {
                throw new IllegalStateException("use of deflateInit after finalizer run!");
            }
            int error = ZLib.deflateInit2(this, level, method, windowBits, memLevel, strategy);
            this.checkError(error);
            isInit = true;
            isDeflate = true;
        }
        
        public synchronized int deflate(int flush) {
            if (!isInit) {
                throw new IllegalStateException("not initialized!");
            }
            if (!isDeflate) {
                throw new IllegalStateException("not in deflate mode!");
            }
            int error = ZLib.deflate(this, flush);
            if (error != Z_STREAM_END) {
                this.checkError(error);
            }
            return error; // either Z_OK or Z_STREAM_END
        }
        
        public synchronized void deflateEnd() {
            if (!isInit) {
                return; // no need to error; just deduplicate request.
            }
            if (!isDeflate) {
                throw new IllegalStateException("not in deflate mode!");
            }
            isInit = false;
            int error = ZLib.deflateEnd(this);
            this.checkError(error);
        }

        public synchronized void inflateInit() {
            if (isInit) {
                throw new IllegalStateException("already initialized!");
            }
            if (!canReinit) {
                throw new IllegalStateException("use of inflateInit after finalizer run!");
            }
            int error = ZLib.inflateInit(this);
            this.checkError(error);
            isInit = true;
            isDeflate = false;
        }

        public synchronized void inflateInit2(int windowBits) {
            if (isInit) {
                throw new IllegalStateException("already initialized!");
            }
            if (!canReinit) {
                throw new IllegalStateException("use of inflateInit after finalizer run!");
            }
            int error = ZLib.inflateInit2(this, windowBits);
            this.checkError(error);
            isInit = true;
            isDeflate = false;
        }
        
        public synchronized int inflate(int flush) {
            if (!isInit) {
                throw new IllegalStateException("not initialized or already closed!");
            }
            if (isDeflate) {
                throw new IllegalStateException("not in inflate mode!");
            }
            int error = ZLib.inflate(this, flush);
            if (error != Z_STREAM_END && error != Z_NEED_DICT) {
                this.checkError(error);
            }
            return error; // either Z_OK or Z_STREAM_END
        }
        
        public synchronized void inflateEnd() {
            if (!isInit) {
                return; // no need to error; just deduplicate request.
            }
            if (isDeflate) {
                throw new IllegalStateException("not in inflate mode!");
            }
            isInit = false;
            int error = ZLib.inflateEnd(this);
            this.checkError(error);
        }
        
        protected synchronized void finalize() {
            if (isInit) {
                if (isDeflate) {
                    this.deflateEnd();
                } else {
                    this.inflateEnd();
                }
            }
            canReinit = false;
        }
    }
    
    public static native String zlibVersion();
    // these methods handle some of the ZStreamPtr contents
    private static native int deflateInit(ZStream strm, int level);
    private static native int deflateInit2(ZStream strm, int level, int method, int windowBits, int memLevel, int strategy);
    private static native int deflate(ZStream strm, int flush);
    private static native int deflateEnd(ZStream strm);

    private static native int inflateInit(ZStream strm);
    private static native int inflateInit2(ZStream strm, int windowBits);
    private static native int inflate(ZStream strm, int flush);
    private static native int inflateEnd(ZStream strm);
}
