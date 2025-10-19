ffmpeg -i input.wmv -vf "scale=-2:1080" -c:v h264 -preset veryfast -crf 23 -c:a aac -b:a 128k -ac 2 -f hls -hls_time 6 -hls_playlist_type vod output.m3u8
