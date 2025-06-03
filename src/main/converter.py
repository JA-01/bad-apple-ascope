import cv2
import json

video_path = "C:\\Users\\Jasee\\Stuff\\bad-apple-ascope\\src\\Videos\\bad_64x64_15fps_full.mp4"
print(video_path)
output_path = "C:\\Users\\Jasee\\Stuff\\bad-apple-ascope\\src\\framedata\\framedata.txt"
print(output_path)

cap = cv2.VideoCapture(video_path)
frame_number = 0

with open(output_path, "w") as f:
    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            break

        height, width, _ = frame.shape
        # f.write(f"{frame_number}\n")

        for y in range(height):
            for x in range(width):
                pixel = frame[y, x]
                brightness = 0.299 * pixel[2] + 0.587 * pixel[1] + 0.114 * pixel[0]
                if brightness < 127.5:
                    f.write(f"{width-x},{y}\n")

        f.write("BREAK\n")  # blank line to separate frames
        print(f"Processed frame {frame_number}")
        frame_number += 1

cap.release()
print(f"Saved black pixel data to {output_path}")
