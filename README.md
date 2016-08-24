# writing-schedule-sync

Takes a writing schedule and uploads it to the user's Google Calendar.

## Usage

Obtain a `client\_secret.json` file according to
[these instructions](https://developers.google.com/google-apps/calendar/quickstart/java#step_1_turn_on_the_api_name),
run `mkdir -p src/main/resources`, and move it into the `src/main/resources`
folder.

Find the calendar ID for the calendar you want to manage (NOTE: must be a
new/empty calendar, as everything will be removed!), at the Google Calendar
API's
[CalendarList](https://developers.google.com/google-apps/calendar/v3/reference/calendarList/list#try-it)
page. It will have the form "lettersandnumbers@group.calendar.google.com."

Set this ID as an environment variable:

```sh
export GCAL_CALENDAR_ID="calid@group.calendar.google.com"
```

Find or create a [pacemaker.press](pacemaker.press) plan and copy its URL.

Then run:

```
export URL="https://pacemaker.press/users/USERNAME/plans/YOUR_PLAN"
sbt "run $URL"
``` 
