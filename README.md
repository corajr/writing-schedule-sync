# writing-schedule-sync

Takes a writing schedule and uploads it to the user's Google Calendar.

## Usage

Obtain a `client\_secret.json` file according to
[these instructions](https://developers.google.com/google-apps/calendar/quickstart/java#step_1_turn_on_the_api_name),
run `mkdir -p src/main/resources`, and move it into the `src/main/resources`
folder.

Then use `sbt run`.
