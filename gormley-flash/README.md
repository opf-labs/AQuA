ffmpeg + winff
handbrake-cli
transmageddon
swftools
swfdec
gnash-tools

baggit

kakadu
openjpeg-tools
jasper, libjasper-runtime

Actions
-------
Find various implementations and try to load. Looking for commonality.
- Flash Video Encoder - Would not load.
- Final Cut Pro - Would not recognise.

http://en.wikipedia.org/wiki/Flash_Video
Ask someone who really groks this stuff.

flvmeta-1.0.11 - also hacked to expose FLVTAG numbers (217)

flvdump (part of flvmeta) diagnosed things just fine.
OPEN: Does the odd block make it stop? Can it be snipped out?

--- Tag #189 at 0x4A210 (303632) ---
Tag type: Video (9)
Body length: 2978
Timestamp: 3010
* Video codec: On2 VP6
* Video frame type: inter frame
Previous tag size: 1212394833
--- Tag #190 at 0x4ADC1 (306625) ---
Tag type: Unknown (217)
Body length: 5089865
Timestamp: 3010100292


0004a210  09 00 0b a2 00 0b c2 00  00 00 00 24 08 f0 59 a3  |...........$..Y.|
0004a220  89 06 c9 9d b0 c4 b7 06  b7 9e 16 67 44 bd 0f fd  |...........gD...|
0004a230  8f 54 e4 1c 56 e8 b7 05  b6 f7 6f 87 e7 ec fb bf  |.T..V.....o.....|
0004a240  7d 5b 26 51 cb 65 3d 99  dc 7e aa 70 11 fa 63 35  |}[&Q.e=..~.p..c5|
0004a250  fb 12 20 7d dd 2c 9a c0  f1 be df c7 d0 f1 11 bb  |.. }.,..........|
0004a260  55 5b 62 c4 1e 5c f9 19  1e 7b f8 3b 46 03 ad 6a  |U[b..\...{.;F..j|
0004a270  0e 92 96 ba d1 85 8b 34  6b 37 a1 dd 1b 54 86 1f  |.......4k7...T..|
0004a280  24 6f 80 20 eb 7b 7d ac  0e ba ed 43 0c 00 0a ef  |$o. .{}....C....|
0004a290  fe 09 d4 69 a5 d9 c5 d8  f8 d2 d9 61 58 af c1 96  |...i.......aX...|
0004a2a0  e4 bc c5 74 22 69 a6 e5  fe 84 43 d8 b6 f4 b8 1a  |...t"i....C.....|
0004a2b0  8e ea 80 0d 74 1b c1 d2  f2 89 86 21 49 3e da 2b  |....t......!I>.+|
0004a2c0  e3 8c 94 ae 84 f2 9d a6  11 51 54 25 b8 46 08 0c  |.........QT%.F..|
0004a2d0  7e 06 98 41 0f 05 d6 6b  f5 00 b4 69 dd ec 5c 13  |~..A...k...i..\.|
0004a2e0  bf cc 87 21 2d 5c e9 ec  40 8c e6 ff f7 85 6e 80  |...!-\..@.....n.|
0004a2f0  b6 4f 51 0b e0 59 70 16  cd 92 15 78 45 db be 06  |.OQ..Yp....xE...|
0004a300  11 85 48 6e b2 5d 91 39  4f 00 85 c5 2f 35 6e dd  |..Hn.].9O.../5n.|
0004a310  ad f3 80 50 c5 5c ae 80  51 84 35 30 0c d9 51 60  |...P.\..Q.50..Q`|
0004a320  3a ee ea 96 b2 79 39 35  19 39 bf a1 26 6c de f2  |:....y95.9..&l..|
0004a330  8c 2e 12 44 c7 64 9f 87  91 e5 21 2f e2 fa 94 ae  |...D.d....!/../..|
0004a340  94 c1 e2 c9 51 62 8f c1  9a 6a c5 c8 dc 5b 7a ae  |....Qb...j...[z.|
0004a350  5e 67 2d f5 e0 81 94 04  0f af 66 a6 ce 64 22 c2  |^g-.......f..d".|
0004a360  78 cf b2 17 74 91 8b e7  e3 ba 28 e5 48 17 4b ff  |x...t.....(.H.K.|
0004a370  c2 9a b7 a7 49 77 18 cf  95 a9 bb 7b 72 f1 17 b1  |....Iw.....{r...|

0x4ADC1

0004adc0  51 d9 4d aa 49 6a 7c 44  b3 f7 63 b9 cc ca 31 e6  |Q.M.Ij|D..c...1.|
0004add0  b0 a6 4e 57 9d 28 f8 e9  c9 9f be 83 10 8f 93 0a  |..NW.(..........|
0004ade0  32 19 1a c7 d3 3e c6 dd  b0 67 4d c0 a4 c3 68 76  |2....>...gM...hv|
0004adf0  df e1 3f 59 49 95 88 52  5a 52 ae 65 cd 73 e2 be  |..?YI..RZR.e.s..|
0004ae00  d5 b2 08 55 1c 1b 11 6f  12 72 10 60 7f 89 17 c0  |...U...o.r.`....|
0004ae10  5b 1d 26 6d d8 7c e5 4a  f7 5f df f9 13 48 26 9e  |[.&m.|.J._...H&.|
0004ae20  76 82 f2 e0 4a 7d c5 f3  56 25 9a ce ab 11 21 2b  |v...J}..V%....!+|
0004ae30  34 bd 85 29 12 44 48 8b  88 84 f8 1c c9 15 55 63  |4..).DH.......Uc|
0004ae40  0a 3f bb 85 37 f8 ec 4c  de 05 c7 c0 72 ea 10 93  |.?..7..L....r...|
0004ae50  9c 50 e5 24 d1 a9 ce b4  5d fd 7c 63 7e 76 09 69  |.P.$....].|c~v.i|
0004ae60  6a 29 db 70 d0 e7 62 cb  64 31 b7 57 bd 32 8f 69  |j).p..b.d1.W.2.i|
0004ae70  cf 1a 8f 4e 38 df 7c 86  22 b0 76 eb b4 c8 64 79  |...N8.|.".v...dy|
0004ae80  7d 35 cd 1a 4a a0 af 06  17 f1 48 68 0a b2 f2 a7  |}5..J.....Hh....|
0004ae90  60 61 89 6f 6b 7d 09 fd  5e 6f 0b 0c 06 50 23 7d  |`a.ok}..^o...P#}|
0004aea0  85 94 64 af a7 90 26 db  66 9d 25 21 6e 67 f4 53  |..d...&.f.%!ng.S|
0004aeb0  96 51 03 76 25 7d 97 42  de ed 60 e1 fc 9f f0 e7  |.Q.v%}.B..`.....|
0004aec0  8f 7b 6b 74 35 9d 0a 00  d9 d3 e4 9d 6e 11 65 d6  |.{kt5.......n.e.|
0004aed0  23 3e 01 5f b7 14 9b 5f  b1 14 47 8d 28 b3 e1 b2  |#>._..._..G.(...|
0004aee0  1a 63 83 35 57 1a 5c 00  d7 3b 4b f3 b2 e8 3a 9f  |.c.5W.\..;K...:.|
0004aef0  d9 e0 d6 69 60 8f a6 99  29 c5 2a 16 dc e7 0f b5  |...i`...).*.....|



