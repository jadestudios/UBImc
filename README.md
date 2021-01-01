![enter image description here](https://raw.githubusercontent.com/thenerdoflight/UBImc/main/UBImc.png)

# Why
This plugin was created as a way to practice Java while screwing up the economy of my Minecraft server even more! 

And yes, this is a band-aid solution to a bigger problem.

# Operations

This plugin needs Vault and an economy plugin to function. 
It will run silently in the background only when there are players on.

This plugin features a manual mode **via console only**.
/runubi - Command to start the UBI process

## UBI Process
A majority of the code checks the player's current balance against thresholds in the config whether it is above the max, below the min, or in-between some. Then it will deposit the amount appropriate for that threshold.  
In all cases, the amount of thresholds should be one less than the deposit amounts. Mainly due to this example:

|Thresholds|  |200000| |100000||50000 | |
|--|--| --| --|--|--|--|--|
|UBI Deposits| 1000 |  | 2000| | 3000| | 4000|

Interpretation: Above 200k gets 1k, between 200k and 100k gets 2k, between 100k and 50k gets 3k, and below 50k gets 4k.

# Plugin Support
 I am currently unsure, but if there is something critical, I might put some time into this again. As of now, it works for the most part.

# Possible/Known Issues
 Not a pure UBI since it requires online and active players.
 No notification to user about UBI

# Forking
This repo has everything you need to throw this into IntelliJ. Like literally... Open Project then BAM
 
