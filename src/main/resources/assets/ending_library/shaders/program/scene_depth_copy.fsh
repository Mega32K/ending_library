#version 150

uniform sampler2D SourceDepth;
out float SavedDepth;

void main() {
    SavedDepth = texelFetch(SourceDepth, ivec2(gl_FragCoord.xy), 0).r;
}
