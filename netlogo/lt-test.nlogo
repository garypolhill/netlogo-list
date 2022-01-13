extensions [lt]

globals [
  empty-list
  list-from-agentset
  list-from-logo-list
  copy-of-a-list
  range-list
  recursive-list
  cursor
  list-from-map
  list-from-multi-arg-map

  n-tests
  n-fails
]

to fail
  set n-fails n-fails + 1
  set n-tests n-tests + 1
end

to pass
  set n-tests n-tests + 1
end

to assert-list-equals [ msg want get ]
  (ifelse not (is-list? want and is-list? get) [
    output-print (word msg ": FAIL (wanted "
      (ifelse-value is-list? want [ "is" ] [ "is not" ]) " a list; got "
      (ifelse-value is-list? get [ "is" ] [ "is not" ]) " a list)")
    fail
  ] length want != length get [
    output-print (word msg ": FAIL (wanted list length " (length want)
      "; got list length " (length get) ")")
    fail
  ] [
    foreach (n-values (length want) [ i -> i ]) [ i ->
      assert-equals (word msg " [" i "]") (item i want) (item i get)
    ]
    pass
  ])
end

to assert-equals [ msg want get ]
  ifelse want = get [
    output-print (word msg ": OK")
    pass
  ] [
    output-print (word msg ": FAIL (wanted \"" want "\"; got \"" get "\")")
    fail
  ]
end

to assert-true [ msg get? ]
  (ifelse not is-boolean? get? [
    output-print (word msg ": FAIL (wanted a boolean, got \"" get? "\")")
    fail
  ] get? [
    output-print (word msg ": OK")
    pass
  ] [
    output-print (word msg ": FAIL (wanted true; got false)")
    fail
  ])
end

to assert-false [ msg get? ]
  (ifelse not is-boolean? get? [
    output-print (word msg ": FAIL (wanted a boolean, got \"" get? "\")")
    fail
  ] get? [
    output-print (word msg ": FAIL (wanted false; got true)")
    fail
  ] [
    output-print (word msg ": OK")
    pass
  ])
end

to setup
  clear-all
  set n-tests 0
  set n-fails 0
  reset-ticks
end

to go
  test-list-construction
  step
  test-access
  step
  test-modification
  step
  test-information
  step
  test-conversion
  step

  if n-fails = 0 [
    ask patches [
      set pcolor green
    ]
  ]
end

to step
  if n-fails > 0 [
    ask patches [
      set pcolor red
    ]
  ]
  tick
end

to test-list-construction
  set empty-list lt:make
  assert-true "empty" lt:empty? empty-list

  set list-from-agentset lt:from-agentset patches
  assert-equals "patches" (count patches) (lt:length list-from-agentset)
  lt:foreach list-from-agentset [ p ->
    assert-true "is-patch?" is-patch? p
    ask p [
      set plabel "x"
    ]
  ]
  ask patches [
    assert-equals "plabels" "x" plabel
    set plabel ""
  ]

  let test-list [ "one" "two" "three" "four" "five" ]
  set list-from-logo-list lt:from-list test-list
  assert-list-equals "from-list" test-list lt:as-list list-from-logo-list

  set range-list lt:range 0 10 1
  assert-list-equals "range" [0 1 2 3 4 5 6 7 8 9 10] lt:as-list range-list

  set list-from-map lt:map [ x -> x + 10 ] range-list
  assert-list-equals "map" [10 11 12 13 14 15 16 17 18 19 20] lt:as-list list-from-map

  set list-from-multi-arg-map (lt:map [ [ x y ] -> x + y ] range-list list-from-map)
  assert-list-equals "map*" [10 12 14 16 18 20 22 24 26 28 30] lt:as-list list-from-multi-arg-map

  set cursor lt:cursor range-list
  assert-true "has-right" lt:has-right? cursor
  assert-false "has-left" lt:has-left? cursor
  assert-equals "right 0" 0 lt:right cursor
  assert-true "has-left" lt:has-left? cursor
  assert-equals "right 1" 1 lt:right cursor
  assert-true "has-left" lt:has-left? cursor
  assert-equals "left 1" 1 lt:left cursor
  assert-true "has-left" lt:has-left? cursor
  repeat 5 [
    let ignore lt:right cursor
  ]
  assert-equals "right 6" 6 lt:right cursor
  set copy-of-a-list lt:copy range-list
  assert-list-equals "copy" lt:as-list range-list lt:as-list copy-of-a-list
  lt:overwrite cursor 16
  assert-equals "copy 6" 6 lt:seventh copy-of-a-list
  assert-equals "overwrite 6" 16 lt:seventh range-list

  set recursive-list lt:from-list [ 1 2 3 ]
  let cpix lt:cursor recursive-list
  lt:insert cpix lt:from-list [ 4 5 6 ]
  assert-equals "insert 0" 1 lt:right cpix
  lt:insert cpix lt:from-list [ 7 8 9 ]
  assert-equals "insert 1" 2 lt:right cpix
  lt:insert cpix lt:from-list (list 10 (lt:from-list [ 11 12 13 ]) 14 )
  assert-equals "insert 2" 3 lt:right cpix

end

to test-access
  let i 0
  let nl-range-list [0 1 2 3 4 5 16 7 8 9 10]
  lt:foreach range-list [ j ->
    assert-equals "foreach" (item i nl-range-list) j
    set i i + 1
  ]
  assert-equals "foreach" 11 i
  assert-equals "first" 0 lt:first range-list
  assert-equals "second" 1 lt:second range-list
  assert-equals "third" 2 lt:third range-list
  assert-equals "fourth" 3 lt:fourth range-list
  assert-equals "fifth" 4 lt:fifth range-list
  assert-equals "sixth" 5 lt:sixth range-list
  assert-equals "seventh" 16 lt:seventh range-list
  assert-equals "eighth" 7 lt:eighth range-list
  assert-equals "ninth" 8 lt:ninth range-list
  assert-equals "tenth" 9 lt:tenth range-list

  ; lt:left and lt:right tested elsewhere

  let nl-flat-list [ 4 5 6 1 7 8 9 2 10 11 12 13 14 3 ]
  set i 0
  lt:foreach-depth-first recursive-list [ j ->
    assert-equals "depth" (item i nl-flat-list) j
    set i i + 1
  ]
end

to test-modification
  lt:cat range-list copy-of-a-list
  assert-true "cat" lt:empty? copy-of-a-list
  assert-list-equals "cat" [0 1 2 3 4 5 16 7 8 9 10 0 1 2 3 4 5 6 7 8 9 10] lt:as-list range-list
  lt:clear range-list
  assert-true "clear" lt:empty? range-list
  set range-list lt:range 0 10 1
  set copy-of-a-list lt:range 11 20 1
  (lt:cat range-list copy-of-a-list lt:range 21 30 1)
  assert-true "cat" lt:empty? copy-of-a-list
  assert-list-equals "cat" n-values 31 [i -> i] lt:as-list range-list
  lt:filter [ i -> i mod 2 = 0 ] range-list
  assert-list-equals "filter" n-values 16 [i -> i * 2] lt:as-list range-list
  let rix lt:cursor range-list
  while [ (lt:right rix) != 8 ] [ ]
  assert-equals "left" 8 lt:left rix
  lt:delete rix
  assert-equals "right" 10 lt:right rix
  assert-equals "left" 10 lt:left rix
  lt:insert rix 8
  assert-list-equals "delete/insert" n-values 16 [i -> i * 2] lt:as-list range-list
  assert-true "is-cursor?" lt:is-cursor? rix
  assert-false "is-cursor?" lt:is-cursor? range-list
  assert-true "is-list?" lt:is-list? range-list
  assert-false "is-list?" lt:is-list? rix
  let stack lt:make
  lt:fpush stack "a"
  assert-list-equals "fpush" ["a"] lt:as-list stack
  (lt:fpush stack "a" "b" "c" "d")
  assert-list-equals "fpush*" ["a" "b" "c" "d" "a"] lt:as-list stack
  lt:fpush-all stack ["a" "b" "c" "d" "e"]
  assert-list-equals "fpush-all" ["a" "b" "c" "d" "e" "a" "b" "c" "d" "a"] lt:as-list stack
  assert-equals "fpop" "a" lt:fpop stack
  assert-list-equals "fpop" ["b" "c" "d" "e" "a" "b" "c" "d" "a"] lt:as-list stack
  lt:lpush stack "z"
  assert-list-equals "lpush" ["b" "c" "d" "e" "a" "b" "c" "d" "a" "z"] lt:as-list stack
  (lt:lpush stack "p" "q" "r")
  assert-list-equals "lpush*" ["b" "c" "d" "e" "a" "b" "c" "d" "a" "z" "p" "q" "r"] lt:as-list stack
  assert-equals "lpop" "r" lt:lpop stack
  assert-list-equals "lpop" ["b" "c" "d" "e" "a" "b" "c" "d" "a" "z" "p" "q"] lt:as-list stack
  lt:lpush-all stack ["p" "q" "r" "s" "t"]
  assert-list-equals "lpush-all" ["b" "c" "d" "e" "a" "b" "c" "d" "a" "z" "p" "q" "p" "q" "r" "s" "t"] lt:as-list stack
  lt:remove-once stack "e"
  assert-list-equals "remove-once" ["b" "c" "d" "a" "b" "c" "d" "a" "z" "p" "q" "p" "q" "r" "s" "t"] lt:as-list stack
  lt:remove stack "d"
  assert-list-equals "remove" ["b" "c" "a" "b" "c" "a" "z" "p" "q" "p" "q" "r" "s" "t"] lt:as-list stack
  lt:remove stack "z"
  assert-list-equals "remove" ["b" "c" "a" "b" "c" "a" "p" "q" "p" "q" "r" "s" "t"] lt:as-list stack
  lt:remove-duplicates stack
  assert-list-equals "remove-dup" ["b" "c" "a" "p" "q" "r" "s" "t"] lt:as-list stack
  lt:reverse stack
  assert-list-equals "reverse" ["t" "s" "r" "q" "p" "a" "c" "b"] lt:as-list stack
  lt:shuffle stack
  assert-true "shuffle" (lt:member? stack "t" "s" "r" "q" "p" "a" "c" "b")
  assert-equals "shuffle" 8 lt:length stack
  lt:sort stack [ [a b] -> ifelse-value (a < b) [-1] [ifelse-value (a > b) [1] [0] ] ]
  assert-list-equals "sort" ["a" "b" "c" "p" "q" "r" "s" "t"] lt:as-list stack
  (lt:fpush stack "u" "v")
  assert-list-equals "fpush" ["u" "v" "a" "b" "c" "p" "q" "r" "s" "t"] lt:as-list stack
  lt:set-first stack 1
  lt:set-second stack 2
  lt:set-third stack 3
  lt:set-fourth stack 4
  lt:set-fifth stack 5
  lt:set-sixth stack 6
  lt:set-seventh stack 7
  lt:set-eighth stack 8
  lt:set-ninth stack 9
  lt:set-tenth stack 10
  assert-list-equals "set-nth" [1 2 3 4 5 6 7 8 9 10] lt:as-list stack
  (lt:keep stack 1 2 3 4 5)
  assert-list-equals "keep*" [1 2 3 4 5] lt:as-list stack
  lt:keep stack 1
  assert-list-equals "keep" [1] lt:as-list stack
  lt:clear stack
  assert-true "clear" lt:empty? stack
end

to test-information
  let stack lt:from-list ["red" "lorry" "yellow" "lorry" "red" "lorry" "yellow" "lorry"]
  let cts lt:counts stack
  foreach cts [ pair ->
    (ifelse item 0 pair = "red" [
      assert-equals "counts" 2 item 1 pair
    ] item 0 pair = "lorry" [
      assert-equals "counts" 4 item 1 pair
    ] item 0 pair = "yellow" [
      assert-equals "counts" 2 item 1 pair
    ] [
      fail
    ])
  ]
  assert-true "deep-member" lt:deep-member? stack "lorry"
  assert-false "deep-member" lt:deep-member? stack "car"
  foreach n-values 20 [i -> i - 2] [ i ->
    ifelse i >= 1 and i <= 14 [
      assert-true "deep-member" lt:deep-member? recursive-list i
    ] [
      assert-false "deep-member" lt:deep-member? recursive-list i
    ]
  ]

  assert-true "member" lt:member? stack "lorry"
  assert-false "member" lt:member? stack "car"
  assert-true "member*" (lt:member? stack "red" "lorry" "yellow")
  assert-false "member*" (lt:member? stack "red" "lorry" "orange")
  assert-true "member-any" (lt:member-any? stack "car" "orange" "red")
  assert-false "member-any" (lt:member-any? stack "car" "orange" "blue")

  let test (sentence n-values 100 [i -> i + 10] n-values 50 [i -> i + 20] n-values 20 [i -> i + 10] n-values 10 [i -> i + 5])
  print (word "z = c(" (reduce [[p i] -> (word p ", " i)] test) ")")
  set stack lt:from-list test
  let hist lt:histogram stack 10 50 10
  assert-list-equals "hist" [ 25 30 20 20 ] hist
  assert-equals "max" max test lt:max stack
  assert-equals "min" min test lt:min stack
  assert-equals "median" median test lt:median stack
  assert-equals "mean" mean test lt:mean stack
  assert-list-equals "modes" modes test lt:modes stack
  assert-equals "sum" sum test lt:sum stack
  assert-equals "quartile" median test item 1 lt:quartiles stack
  assert-list-equals "quartile" [ 24.5 44.5 67.0 ] lt:quartiles stack ; from fivenum() in R
end

to test-conversion
  ; lt:as-list already extensively tested
  assert-list-equals "as-list-deeply" [ [4 5 6] 1 [7 8 9] 2 [10 [11 12 13] 14] 3 ] lt:as-list-deeply recursive-list
end
@#$#@#$#@
GRAPHICS-WINDOW
210
10
647
32
-1
-1
13.0
1
10
1
1
1
0
1
1
1
0
32
0
0
0
0
1
ticks
30.0

OUTPUT
210
38
647
430
13

MONITOR
44
26
102
71
NIL
n-tests
17
1
11

MONITOR
104
26
161
71
NIL
n-fails
17
1
11

BUTTON
43
73
102
106
NIL
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
104
73
161
106
NIL
go
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

PLOT
8
111
208
261
failures
NIL
NIL
0.0
10.0
0.0
10.0
true
false
"" ""
PENS
"n-fails" 1.0 0 -16777216 true "" "plot n-fails"

@#$#@#$#@
## WHAT IS IT?

(a general understanding of what the model is trying to show or explain)

## HOW IT WORKS

(what rules the agents use to create the overall behavior of the model)

## HOW TO USE IT

(how to use the model, including a description of each of the items in the Interface tab)

## THINGS TO NOTICE

(suggested things for the user to notice while running the model)

## THINGS TO TRY

(suggested things for the user to try to do (move sliders, switches, etc.) with the model)

## EXTENDING THE MODEL

(suggested things to add or change in the Code tab to make the model more complicated, detailed, accurate, etc.)

## NETLOGO FEATURES

(interesting or unusual features of NetLogo that the model uses, particularly in the Code tab; or where workarounds were needed for missing features)

## RELATED MODELS

(models in the NetLogo Models Library and elsewhere which are of related interest)

## CREDITS AND REFERENCES

(a reference to the model's URL on the web if it has one, as well as any other necessary credits, citations, and links)
@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

sheep
false
15
Circle -1 true true 203 65 88
Circle -1 true true 70 65 162
Circle -1 true true 150 105 120
Polygon -7500403 true false 218 120 240 165 255 165 278 120
Circle -7500403 true false 214 72 67
Rectangle -1 true true 164 223 179 298
Polygon -1 true true 45 285 30 285 30 240 15 195 45 210
Circle -1 true true 3 83 150
Rectangle -1 true true 65 221 80 296
Polygon -1 true true 195 285 210 285 210 240 240 210 195 210
Polygon -7500403 true false 276 85 285 105 302 99 294 83
Polygon -7500403 true false 219 85 210 105 193 99 201 83

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

wolf
false
0
Polygon -16777216 true false 253 133 245 131 245 133
Polygon -7500403 true true 2 194 13 197 30 191 38 193 38 205 20 226 20 257 27 265 38 266 40 260 31 253 31 230 60 206 68 198 75 209 66 228 65 243 82 261 84 268 100 267 103 261 77 239 79 231 100 207 98 196 119 201 143 202 160 195 166 210 172 213 173 238 167 251 160 248 154 265 169 264 178 247 186 240 198 260 200 271 217 271 219 262 207 258 195 230 192 198 210 184 227 164 242 144 259 145 284 151 277 141 293 140 299 134 297 127 273 119 270 105
Polygon -7500403 true true -1 195 14 180 36 166 40 153 53 140 82 131 134 133 159 126 188 115 227 108 236 102 238 98 268 86 269 92 281 87 269 103 269 113

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270
@#$#@#$#@
NetLogo 6.2.1
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
<experiments>
  <experiment name="test" repetitions="1" runMetricsEveryStep="true">
    <setup>setup
go</setup>
    <timeLimit steps="6"/>
    <metric>n-fails</metric>
  </experiment>
</experiments>
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 0.0 1.0
0.0 1 1.0 0.0
0.2 0 0.0 1.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180
@#$#@#$#@
0
@#$#@#$#@
