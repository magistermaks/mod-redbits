# RedBits help page

## Recipes

All recipes are available in game, in the "crafting book", 
but are initially hidden. To unlock all of them use this command 
`/recipe give @s *`

## Gates

**Detector**  
Is a falling/rising edge detector, in rising mode it triggers
(like an observer, emitting a short signal) when input turns on 
and in the falling mode it triggers when input turns off. 
The mode can be toggled be clicking on the gate.

**Switcher**  
Works like a redstone lever that can be switched by powering it (it can also be switched by clicking
on it).

**Inverter**  
Works like vanilla redstone torch but in the form of a redstone gate,
with no delay control (delay is always equal to 1 redstone tick).

**Latch**  
Has two outputs, one of which is always active while the other inactive,
this state can be flipped by clicking on the block or by powering the
inactive side.

**Timer**  
When powered will periodically activate emitting a pulse,
the delay between those pulses and their length can be regulated
by clicking on the timer. Available timings: 1, 2, 4, and 8 redstone ticks.

**Two Way Repeater**  
Works like vanilla repeater but doesn't have timing control,
(delay is always equal 1 redstone tick) and allows signals to
pass in both directions.

## Input

**Large Buttons**  
They work exactly the same as they vanilla counterparts, but are bigger.

**Pressure Plates**  
Those pressure plates are activated in the following conditions:
- Obsidian pressure plates are activated by players.
- Crying obsidian pressure plates are activated by hostile mobs.
- End stone pressure plates are activated by villagers.
- Basalt pressure plates are activated by tamed animals.

**Sight Sensor**  
Activated by players looking at it, emits a short redstone pulse. 
Maximum activation distance is 128 blocks by default (This can be tweaked in the mod settings).

## Lamps

**Shaded Lamp**  
Works like vanilla redstone lamp, but it doesn't emit light. 
Because of this it's a perfect fit for blinking controls and large displays 
as the performance impact of activating those lamps is much smaller.

**Color Lamp**  
Depending on the strength of the input signal it changes its color.

## Other

**Redstone Emitter**  
Emits comparator signal whose strength can be regulated by clicking on the block, 
replacement for using cauldrons and composters in circuits.

**Inverted Redstone Torch**  
Redstone torch that doesn't invert the signal.

> This help page may not be 100% accurate.