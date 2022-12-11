Trivial console app for training with Docker volumes.

docker build -t personal-notes .

docker run -it --rm --name personal-notes -v files:/app/files personal-notes
