#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D SceneDepthSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform mat4 InvSceneProjMat;
uniform vec2 InSize;
uniform float Progress;

out vec4 fragColor;


float grey(vec3 color) {
    return clamp(color.r * 0.299 + color.g * 0.587 + color.b * 0.114, 0.0, 1.0);
}
float sat( float t ) {
    return clamp( t, 0.0, 1.0 );
}

vec2 sat( vec2 t ) {
    return clamp( t, 0.0, 1.0 );
}

//remaps inteval [a;b] to [0;1]
float remap  ( float t, float a, float b ) {
    return sat( (t - a) / (b - a) );
}

//note: /\ t=[0;0.5;1], y=[0;1;0]
float linterp( float t ) {
    return sat( 1.0 - abs( 2.0*t - 1.0 ) );
}

vec3 spectrum_offset( float t ) {
    vec3 ret;
    float lo = step(t,0.5);
    float hi = 1.0-lo;
    float w = linterp( remap( t, 1.0/6.0, 5.0/6.0 ) );
    float neg_w = 1.0-w;
    ret = vec3(lo,1.0,hi) * vec3(neg_w, w, neg_w);
    return pow( ret, vec3(1.0/2.2) );
}

//note: [0;1]
float rand( vec2 n ) {
    return fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}

//note: [-1;1]
float srand( vec2 n ) {
    return rand(n) * 2.0 - 1.0;
}

float mytrunc( float x, float num_levels )
{
    return floor(x*num_levels) / num_levels;
}
vec2 mytrunc( vec2 x, float num_levels )
{
    return floor(x*num_levels) / num_levels;
}

vec4 glitch(float GLITCH)
{
    vec2 uv = texCoord;
    uv.y = uv.y;

    float time = mod(Progress*100.0, 32.0)/110.0; // + modelmat[0].x + modelmat[0].z;

    float gnm = sat( GLITCH );
    float rnd0 = rand( mytrunc( vec2(time, time), 6.0 ) );
    float r0 = sat((1.0-gnm)*0.7 + rnd0);
    float rnd1 = rand( vec2(mytrunc( uv.x, 10.0*r0 ), time) ); //horz
    //float r1 = 1.0f - sat( (1.0f-gnm)*0.5f + rnd1 );
    float r1 = 0.5 - 0.5 * gnm + rnd1;
    r1 = 1.0 - max( 0.0, ((r1<1.0) ? r1 : 0.9999999) ); //note: weird ass bug on old drivers
    float rnd2 = rand( vec2(mytrunc( uv.y, 40.0*r1 ), time) ); //vert
    float r2 = sat( rnd2 );

    float rnd3 = rand( vec2(mytrunc( uv.y, 10.0*r0 ), time) );
    float r3 = (1.0-sat(rnd3+0.8)) - 0.1;

    float pxrnd = rand( uv + time );

    float ofs = 0.05 * r2 * GLITCH * ( rnd0 > 0.5 ? 1.0 : -1.0 );
    ofs += 0.5 * pxrnd * ofs;

    uv.y += 0.1 * r3 * GLITCH;

    const int NUM_SAMPLES = 20;
    const float RCP_NUM_SAMPLES_F = 1.0 / float(NUM_SAMPLES);

    vec4 sum = vec4(0.0);
    vec3 wsum = vec3(0.0);
    for( int i=0; i<NUM_SAMPLES; ++i )
    {
        float t = float(i) * RCP_NUM_SAMPLES_F;
        uv.x = sat( uv.x + ofs * t );
        vec4 samplecol = texture( DiffuseSampler, uv, -10.0 );
        vec3 s = spectrum_offset( t );
        samplecol.rgb = samplecol.rgb * s;
        sum += samplecol;
        wsum += s;
    }
    sum.rgb /= wsum;
    sum.a *= RCP_NUM_SAMPLES_F;

    return sum;
    // * outcol0.a;
}
vec4 glitchGrey(float GLITCH)
{
    vec2 uv = texCoord;
    uv.y = uv.y;

    float time = mod(Progress*100.0, 32.0)/110.0; // + modelmat[0].x + modelmat[0].z;

    float gnm = sat( GLITCH );
    float rnd0 = rand( mytrunc( vec2(time, time), 6.0 ) );
    float r0 = sat((1.0-gnm)*0.7 + rnd0);
    float rnd1 = rand( vec2(mytrunc( uv.x, 10.0*r0 ), time) ); //horz
    //float r1 = 1.0f - sat( (1.0f-gnm)*0.5f + rnd1 );
    float r1 = 0.5 - 0.5 * gnm + rnd1;
    r1 = 1.0 - max( 0.0, ((r1<1.0) ? r1 : 0.9999999) ); //note: weird ass bug on old drivers
    float rnd2 = rand( vec2(mytrunc( uv.y, 40.0*r1 ), time) ); //vert
    float r2 = sat( rnd2 );

    float rnd3 = rand( vec2(mytrunc( uv.y, 10.0*r0 ), time) );
    float r3 = (1.0-sat(rnd3+0.8)) - 0.1;

    float pxrnd = rand( uv + time );

    float ofs = 0.05 * r2 * GLITCH * ( rnd0 > 0.5 ? 1.0 : -1.0 );
    ofs += 0.5 * pxrnd * ofs;

    uv.y += 0.1 * r3 * GLITCH;

    const int NUM_SAMPLES = 20;
    const float RCP_NUM_SAMPLES_F = 1.0 / float(NUM_SAMPLES);

    vec4 sum = vec4(0.0);
    vec3 wsum = vec3(0.0);
    for( int i=0; i<NUM_SAMPLES; ++i )
    {
        float t = float(i) * RCP_NUM_SAMPLES_F;
        uv.x = sat( uv.x + ofs * t );
        vec4 samplecol = vec4(vec3(grey(texture( DiffuseSampler, uv, -10.0 ).rgb)), 1.0);
        vec3 s = spectrum_offset( t );
        samplecol.rgb = samplecol.rgb * s;
        sum += samplecol;
        wsum += s;
    }
    sum.rgb /= wsum;
    sum.a *= RCP_NUM_SAMPLES_F;

    return sum;
    // * outcol0.a;
}
vec3 reconstructViewPosition(vec2 uv, float depth) {
    vec4 clip = vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    vec4 view = InvSceneProjMat * clip;
    return view.xyz / view.w;
}
void main() {
    float depth = texture(SceneDepthSampler, texCoord).r;
    vec4 tex = texture(DiffuseSampler, texCoord);
    vec3 viewPosition = reconstructViewPosition(texCoord, depth) / 16.0;
    float distanceToCamera = length(viewPosition);
    float progress = Progress * Progress;
    float distance2 = distanceToCamera - progress;
    if (distanceToCamera >= progress) {
        fragColor = tex;
    } else {
        fragColor = vec4(vec3(grey(tex.rgb)), tex.a);
    }
    float width = length(oneTexel) * 24.0;
    float distanceToProgress = abs(distance2);
    float widthMultipler = 4.5;
    if (distanceToProgress < width * widthMultipler) {
        if (distanceToProgress <= width) {
            vec4 textureGrey = fragColor = vec4(vec3(grey(tex.rgb)), tex.a);
            vec4 textureNormal = tex;
            if (distance2 >= 0) {
                fragColor = vec4(1.0 - smoothstep(0.0, 1.0, distance2 / width)) + textureNormal;
            } else {
                fragColor = vec4(1.0 - smoothstep(0.0, 1.0, distanceToProgress / width)) + textureGrey;
            }
        }
        if (distanceToProgress - width > 0.0) {
            if (distance2 >= 0) {
                fragColor = glitch((distanceToCamera - width) / width / (widthMultipler - 1.0) * 0.4);
            } else {
                fragColor = glitchGrey((distanceToCamera - width) / width / (widthMultipler - 1.0) * 0.4);
            }

        }
    }
}
