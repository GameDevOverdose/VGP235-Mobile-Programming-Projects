# VGP235-Intro to Mobile Programming 📱

Hello Guys! __Ayush Vaibhav Goyal__ here. This course, taught by Professor [Amir Jahanlou](https://github.com/AmirJahan), is my first introduction to app development and programming for mobile, so it has been quite a learning and I thought I'd share some of the projects here! (and totally not because we're being offered, 3 extra points on our midterm to make this repo public and half-presentable)

# Midterm 📄

The idea for the midterm was on paper quite simple, a pseudo dating/discovery app spanning 3 activities (or pages) in the span of 4 hours.

1) The **Home Page**, is where the user lands, being the simplest of the three, it only contains the user's data, and buttons to setup/modify the user profile and if that's done go explore.

2) The **User Settings Page**, where the user can create and edit a myriad of details about themselves such as Name, Occupation, Age, Weight, Height, Eye Color, Hair Color etc. Details that have some bearing on determining wether it's a match or not.

3) The **Explore Page**, is the one most similar to current dating apps, but it basically displays a fake user's profile with randomly generated/selected details, and the page then weighs out the two user profiles through an arbitrary algorithm to see wether it's a match or not.

| Home | User Settings | Discovery |
|---|---|---|
| <img src="https://github.com/user-attachments/assets/daa272b7-eb7e-4fe4-945b-f61978ce9a24" width="250"/> | <img src="https://github.com/user-attachments/assets/914a0f04-65a2-4fa4-9a12-e314ee17dc89" width="250"/> | <img src="https://github.com/user-attachments/assets/7864ce0d-ccb0-4c26-b061-0b0124ed8793" width="250"/> |

While hectic, the app still somehow came together towards the end, which even though it's pretty simple, still was somewhat off a relief to pull off considering 3 of the 4 hours were mostly spent setting up the code and dealing with the emulator, as well as the slightly clunky and painfully slow layout editors leaving only an hour for the design and the look of the UI.

# Things to Improve 🎯

- UI and the overall aesthetic leaves a lot to be desired. White space, awkward formatting and inconsistent visuals would be the highest priority in things that need fixing.
- Out of the list of requirements that needed implementing, one thing that I entirely skipped was keywords (basically hobbies/interest) for both users, which led to a basic algorithm implementation since those keywords would have the highest weightage otherwise when trying to determine a match.
- The details that the users input in the settings were meant to be displayed on the home page apart from that, the discover users' details would benefit from being labelled.
- A nice to have, would be the match system hinting and displaying what factors led it to come to the conclusion that it did.

and many more I imagine, but these were the primary ones.

# Final 📄

For the final project, we were offered two choices. The first, to create a simple app with the knowledge that we have gained throughout the course. The second? To create a more impressive app completely using AI. Having only a week to develop the app, and the prospect of reaching beyond what I already know sounded much better to me, so the second approach is what I chose.

And what came of it is A Steam Game Recommendation app that reads your Steam profile, and allow you to filter games (or not) and get recommendations based off of those games.

# Implementation

- The first part of recommending a game is finding the player's taste and preferences. The way the algorithm currently is that it tries to find the most commonly occuring genres and game tags within all the games you've played or selected for, and makes a small list of the player's Gaming DNA. The genres are all found using Steam's API but since they tend to be too broad to be meaningful, the app scrapes the tags off of a game's steam page using it's AppID.

- Once the app has stored that list, it then uses 3 random tags/genres and looks them up on the Steam's search. Using a variety of different identification methods to vary the results such as finding a cult classic, or a blockbuster, or a hidden gem, and slightly varying the number of the pick from the results found, the App then simply picks 3 games for the 3 selected genres.

- The profile display, simply calls the Steam API and get's the player's information using their SteamID.

# Learnings

- The Leaky Bucket Problem: When developing the algorithm, a peculiar issue that I ran into was after clicking the recommend button 4-5 times, the recommendations just disappread and just wouldn't appear until the user's 17 click (or after waiting a minute or two). But soon after some diagnosis what I quickly realized was that the brickwall I was running into was Steam's bot detection. What solved it was desiging the algorithm to be more frugal when it comes to sending requests to the website, so where it had been previously doing it 9 times, for a single recommendation it now does it only 3 times, allowing the app to run a reasonably of time while the bucket refills.

Basically sending 

# Links 🔗

[![Itch.io](https://img.shields.io/badge/itch.io-%23FF0B34.svg?logo=Itch.io&logoColor=white)](https://gamedevoverdose.itch.io/)  [![LinkedIn](https://img.shields.io/badge/Linkedin-%230077B5.svg?logo=linkedin&logoColor=white)](https://ca.linkedin.com/in/ayush-vaibhav-goyal)
