# Install Mongo om MacOS:

## Install Homebrew (if you don't have)
```
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```

## Install MongoDB
```
brew tap mongodb/brew
brew install mongodb-community@4.2
```

## Install Robo3t
Download from `https://robomongo.org/download`

# Run Database
All commands are from `db` folder
```
cd db
```

## Permissions
```
chmod +x db
```

## Start
Run `./db start` to get mongo up and running on `localhost:27018`.

## Stop
Run `./db stop` to stop DB server

## Wipe
Run `./db wipe` to wipe out the DB and start fresh.

## Restore
Run `./db restore` to restore DB with existing data.  
Run this command for the first time see the app with some content.

## Take DB dump
Run `./db dump` to take a new DB dump
