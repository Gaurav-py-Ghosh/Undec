package com.example.undec;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsActivity extends BaseActivity {

    private static final String TAG = "EventsActivity";
    private RecyclerView eventsRecyclerView;
    private List<Event> events = new ArrayList<>();
    private EventsAdapter eventsAdapter;
    private FloatingActionButton fabAddEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        
        try {
            // Setup toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Events & Meetings");
            
            // Initialize RecyclerView
            eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
            eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            
            // Setup bottom navigation
            setupBottomNavigation(R.id.nav_home); // No specific events tab, so home is used
            
            // Add FAB for adding events
            fabAddEvent = findViewById(R.id.fabAddEvent);
            fabAddEvent.setOnClickListener(v -> showAddEventDialog());
            
            // Load sample events if none exist
            loadEvents();
            
            // Set up the adapter with all events initially
            eventsAdapter = new EventsAdapter(new ArrayList<>(events));
            eventsRecyclerView.setAdapter(eventsAdapter);
            
            // Update event counter text
            updateEventCountText();
            
            // Setup filter tabs
            setupFilterTabs();
            
            Log.d(TAG, "EventsActivity setup complete");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing EventsActivity: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading events screen", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_home; // No specific events tab, so home is used
    }
    
    private void setupFilterTabs() {
        TabLayout tabLayout = findViewById(R.id.eventsTabLayout);
        if (tabLayout != null) {
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    filterEventsByTab(tab.getPosition());
                }
                
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    // Not needed
                }
                
                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    // Refresh the filter
                    filterEventsByTab(tab.getPosition());
                }
            });
        }
    }
    
    private void filterEventsByTab(int tabPosition) {
        List<Event> filteredEvents = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        
        // Reset time to start of day
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfToday = calendar.getTime();
        
        // Set to end of day
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endOfToday = calendar.getTime();
        
        // Calculate start of week
        calendar.setTime(startOfToday);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date startOfWeek = calendar.getTime();
        
        // Calculate end of week
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfWeek = calendar.getTime();
        
        // Calculate start of month
        calendar.setTime(startOfToday);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = calendar.getTime();
        
        // Calculate end of month
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfMonth = calendar.getTime();
        
        // Filter events based on tab position
        switch (tabPosition) {
            case 0: // All
                filteredEvents.addAll(events);
                break;
            case 1: // Today
                for (Event event : events) {
                    Date eventDate = event.getDateTime();
                    if (eventDate.compareTo(startOfToday) >= 0 && eventDate.compareTo(endOfToday) <= 0) {
                        filteredEvents.add(event);
                    }
                }
                break;
            case 2: // This Week
                for (Event event : events) {
                    Date eventDate = event.getDateTime();
                    if (eventDate.compareTo(startOfWeek) >= 0 && eventDate.compareTo(endOfWeek) <= 0) {
                        filteredEvents.add(event);
                    }
                }
                break;
            case 3: // This Month
                for (Event event : events) {
                    Date eventDate = event.getDateTime();
                    if (eventDate.compareTo(startOfMonth) >= 0 && eventDate.compareTo(endOfMonth) <= 0) {
                        filteredEvents.add(event);
                    }
                }
                break;
        }
        
        // Update the adapter with filtered events
        eventsAdapter.updateEvents(filteredEvents);
        
        // Update event counter with filtered count
        updateEventCountText(filteredEvents.size());
    }
    
    private void updateEventCountText() {
        updateEventCountText(events.size());
    }
    
    private void updateEventCountText(int count) {
        TextView eventCountText = findViewById(R.id.eventCountText);
        if (eventCountText != null) {
            eventCountText.setText("You have " + count + " upcoming meeting" + (count != 1 ? "s" : ""));
        }
    }
    
    private void loadEvents() {
        // Add sample events if the list is empty
        if (events.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            
            // Today's meeting
            calendar.set(Calendar.HOUR_OF_DAY, 10);
            calendar.set(Calendar.MINUTE, 0);
            Event event1 = new Event(
                    "Project Kickoff Meeting",
                    calendar.getTime(),
                    60, // 60 minutes
                    "Team discussion about project goals and milestones",
                    "John, Sarah, Mike",
                    "Google Meet",
                    "https://meet.google.com/abc-defg-hij"
            );
            events.add(event1);
            
            // Tomorrow's meeting
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 14);
            calendar.set(Calendar.MINUTE, 30);
            Event event2 = new Event(
                    "Client Presentation",
                    calendar.getTime(),
                    90, // 90 minutes
                    "Presenting design concepts to the client",
                    "Emma, David, Client Team",
                    "Zoom",
                    "https://zoom.us/j/1234567890"
            );
            events.add(event2);
            
            // Next week's meeting
            calendar.add(Calendar.DAY_OF_MONTH, 5);
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 15);
            Event event3 = new Event(
                    "Sprint Planning",
                    calendar.getTime(),
                    120, // 120 minutes
                    "Planning tasks for the next sprint",
                    "All team members",
                    "Microsoft Teams",
                    "https://teams.microsoft.com/meeting/join"
            );
            events.add(event3);
        }
    }
    
    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_event, null);
        builder.setView(dialogView);
        
        EditText titleInput = dialogView.findViewById(R.id.eventTitleInput);
        TextView dateTimeText = dialogView.findViewById(R.id.eventDateTime);
        EditText durationInput = dialogView.findViewById(R.id.eventDuration);
        EditText descriptionInput = dialogView.findViewById(R.id.eventDescription);
        EditText participantsInput = dialogView.findViewById(R.id.eventParticipants);
        EditText platformInput = dialogView.findViewById(R.id.eventPlatform);
        EditText linkInput = dialogView.findViewById(R.id.eventLink);
        Button dateTimeButton = dialogView.findViewById(R.id.btnSelectDateTime);
        
        final Calendar eventCalendar = Calendar.getInstance();
        updateDateTimeText(dateTimeText, eventCalendar);
        
        // Set up date/time picker
        dateTimeButton.setOnClickListener(v -> {
            // Show date picker
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EventsActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        eventCalendar.set(Calendar.YEAR, year);
                        eventCalendar.set(Calendar.MONTH, month);
                        eventCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        
                        // After date is picked, show time picker
                        new TimePickerDialog(
                                EventsActivity.this,
                                (timeView, hourOfDay, minute) -> {
                                    eventCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    eventCalendar.set(Calendar.MINUTE, minute);
                                    updateDateTimeText(dateTimeText, eventCalendar);
                                },
                                eventCalendar.get(Calendar.HOUR_OF_DAY),
                                eventCalendar.get(Calendar.MINUTE),
                                false
                        ).show();
                    },
                    eventCalendar.get(Calendar.YEAR),
                    eventCalendar.get(Calendar.MONTH),
                    eventCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
        
        builder.setPositiveButton("Add Event", (dialog, which) -> {
            // Validate inputs
            String title = titleInput.getText().toString().trim();
            String durationStr = durationInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String participants = participantsInput.getText().toString().trim();
            String platform = platformInput.getText().toString().trim();
            String link = linkInput.getText().toString().trim();
            
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int duration;
            try {
                duration = Integer.parseInt(durationStr);
            } catch (NumberFormatException e) {
                duration = 60; // Default to 60 minutes
            }
            
            // Create and add the new event
            Event newEvent = new Event(title, eventCalendar.getTime(), duration, 
                    description, participants, platform, link);
            events.add(0, newEvent); // Add to the beginning of the list
            eventsAdapter.notifyItemInserted(0);
            eventsRecyclerView.scrollToPosition(0);
            
            // Update event counter
            updateEventCountText();
            
            Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    private void updateDateTimeText(TextView textView, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault());
        textView.setText(sdf.format(calendar.getTime()));
    }
    
    // Event model class
    public static class Event {
        private String title;
        private Date dateTime;
        private int durationMinutes;
        private String description;
        private String participants;
        private String platform;
        private String meetingLink;
        
        public Event(String title, Date dateTime, int durationMinutes, String description,
                    String participants, String platform, String meetingLink) {
            this.title = title;
            this.dateTime = dateTime;
            this.durationMinutes = durationMinutes;
            this.description = description;
            this.participants = participants;
            this.platform = platform;
            this.meetingLink = meetingLink;
        }
        
        public String getTitle() {
            return title;
        }
        
        public Date getDateTime() {
            return dateTime;
        }
        
        public String getFormattedDateTime() {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d 'at' h:mm a", Locale.getDefault());
            return sdf.format(dateTime);
        }
        
        public String getFormattedDuration() {
            if (durationMinutes < 60) {
                return durationMinutes + " min";
            } else {
                int hours = durationMinutes / 60;
                int mins = durationMinutes % 60;
                if (mins == 0) {
                    return hours + " hr";
                } else {
                    return hours + " hr " + mins + " min";
                }
            }
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getParticipants() {
            return participants;
        }
        
        public String getPlatform() {
            return platform;
        }
        
        public String getMeetingLink() {
            return meetingLink;
        }
    }
    
    // RecyclerView adapter for events
    private class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
        private List<Event> eventsList;
        
        public EventsAdapter(List<Event> eventsList) {
            this.eventsList = eventsList;
        }
        
        // Add method to update events list for filtering
        public void updateEvents(List<Event> newEvents) {
            this.eventsList = new ArrayList<>(newEvents);
            notifyDataSetChanged();
        }
        
        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_event, parent, false);
            return new EventViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            Event event = eventsList.get(position);
            holder.bind(event);
            
            // Set click listener for Join Meeting button
            Button joinButton = holder.itemView.findViewById(R.id.btnJoinMeeting);
            if (joinButton != null) {
                joinButton.setOnClickListener(v -> {
                    openMeetingLink(event);
                });
            }
            
            // Set click listener for Share button
            holder.itemView.findViewById(R.id.btnShare).setOnClickListener(v -> {
                shareEvent(event);
            });
        }
        
        private void openMeetingLink(Event event) {
            if (event.getMeetingLink() != null && !event.getMeetingLink().isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(android.net.Uri.parse(event.getMeetingLink()));
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening meeting link: " + e.getMessage(), e);
                    Toast.makeText(EventsActivity.this, 
                            "Could not open link: " + event.getMeetingLink(), 
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EventsActivity.this, "No meeting link available", Toast.LENGTH_SHORT).show();
            }
        }
        
        private void shareEvent(Event event) {
            String shareText = event.getTitle() + "\n" +
                    "When: " + event.getFormattedDateTime() + " (" + event.getFormattedDuration() + ")\n" +
                    (event.getDescription() != null && !event.getDescription().isEmpty() ? 
                            "Description: " + event.getDescription() + "\n" : "") +
                    (event.getParticipants() != null && !event.getParticipants().isEmpty() ? 
                            "With: " + event.getParticipants() + "\n" : "") +
                    (event.getPlatform() != null && !event.getPlatform().isEmpty() ? 
                            "Platform: " + event.getPlatform() + "\n" : "") +
                    (event.getMeetingLink() != null && !event.getMeetingLink().isEmpty() ? 
                            "Link: " + event.getMeetingLink() : "");
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Meeting: " + event.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share meeting details"));
        }
        
        @Override
        public int getItemCount() {
            return eventsList.size();
        }
        
        class EventViewHolder extends RecyclerView.ViewHolder {
            private TextView titleText;
            private TextView dateTimeText;
            private TextView durationText;
            private TextView descriptionText;
            private TextView participantsText;
            private Chip platformChip;
            
            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                titleText = itemView.findViewById(R.id.eventTitle);
                dateTimeText = itemView.findViewById(R.id.eventDateTime);
                durationText = itemView.findViewById(R.id.eventDuration);
                descriptionText = itemView.findViewById(R.id.eventDescription);
                participantsText = itemView.findViewById(R.id.eventParticipants);
                platformChip = itemView.findViewById(R.id.eventPlatform);
            }
            
            public void bind(Event event) {
                titleText.setText(event.getTitle());
                dateTimeText.setText(event.getFormattedDateTime());
                durationText.setText(event.getFormattedDuration());
                
                if (event.getDescription() != null && !event.getDescription().isEmpty()) {
                    descriptionText.setText(event.getDescription());
                    descriptionText.setVisibility(View.VISIBLE);
                } else {
                    descriptionText.setVisibility(View.GONE);
                }
                
                if (event.getParticipants() != null && !event.getParticipants().isEmpty()) {
                    participantsText.setText("With: " + event.getParticipants());
                    participantsText.setVisibility(View.VISIBLE);
                } else {
                    participantsText.setVisibility(View.GONE);
                }
                
                if (event.getPlatform() != null && !event.getPlatform().isEmpty()) {
                    platformChip.setText(event.getPlatform());
                    platformChip.setVisibility(View.VISIBLE);
                    
                    // Set background color based on event platform
                    int colorRes = R.color.primary_color; // Default color
                    String platform = event.getPlatform().toLowerCase();
                    if (platform.contains("zoom")) {
                        colorRes = R.color.zoom_color;
                    } else if (platform.contains("google") || platform.contains("meet")) {
                        colorRes = R.color.google_meet_color;
                    } else if (platform.contains("teams") || platform.contains("microsoft")) {
                        colorRes = R.color.ms_teams_color;
                    }
                    platformChip.setChipBackgroundColorResource(colorRes);
                } else {
                    platformChip.setVisibility(View.GONE);
                }
            }
        }
    }
} 