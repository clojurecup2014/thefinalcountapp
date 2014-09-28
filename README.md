# The Final Count App

![Logo](https://raw.githubusercontent.com/clojurecup2014/thefinalcountapp/master/resources/public/images/logo.png?token=3418223__eyJzY29wZSI6IlJhd0Jsb2I6Y2xvanVyZWN1cDIwMTQvdGhlZmluYWxjb3VudGFwcC9tYXN0ZXIvcmVzb3VyY2VzL3B1YmxpYy9pbWFnZXMvbG9nby5wbmciLCJleHBpcmVzIjoxNDEyNDk1NTE0fQ%3D%3D--696db94968885a5f37e099d7031355aaa343d689 "Logo")

This an application for your team to track different things in a funny way. For example, you can keep track of the times you've broken your git repo or someone said something funny.

The UI was designed to be displayed in a big screen (like a TV in your office).

This project was done on the Clojure Cup contest (September 2014)

https://clojurecup.com

## Features

* Create different flavours of counters
 - "Days without", for counting the days that have passed since the last time something occurred
 - "Days doing", for counting the streak of days you've done something
 - "Times", a counter that can be incremented and tracks the number of times something has happened
* Reset counters
* SVG rendering and visualization of counters
* "Real time" updates through websockets

# Third-party libraries

* Clojure & Clojurescript (obviously)
* core/async - to create a cool comunication API in the front-end
* liberator and transit - We created a Liberator API and transit as the format protocol
* stuartsierra's Component - To structure our back-end API
* reagent - to use React.js with Clojurescript
* Schema - for validation server and client side
* sente - robust websocket communication between client and server

## License

Distributed under the  GNU AFFERO GENERAL PUBLIC LICENSE v3

## Authors

Alonso Torres and Alejandro Gómez
