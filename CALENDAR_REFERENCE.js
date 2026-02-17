// CALENDAR QUICK REFERENCE GUIDE
// ================================

// 1. CALENDAR STATE MANAGEMENT
// -----------------------------
currentMonth = signal(new Date().getMonth());      // 0-11 (Jan-Dec)
currentYear = signal(new Date().getFullYear());    // e.g., 2026
calendarView = signal < 'month' | 'week' > ('month');  // View mode

// 2. NAVIGATION METHODS
// ---------------------
previousMonth()  // Go to previous month
nextMonth()      // Go to next month
goToToday()      // Jump to current date

// 3. CALENDAR GRID GENERATION
// ---------------------------
calendarDays = computed(() => {
    // Returns array of 42 CalendarDay objects (6 weeks × 7 days)
    // Each CalendarDay contains:
    // - date: Date object
    // - day: Day number (1-31)
    // - isCurrentMonth: boolean
    // - isToday: boolean
    // - interviews: Interview[] for that day
});

// 4. INTERVIEW DATA STRUCTURE
// ---------------------------
interface Interview {
    id: number;
    candidate: string;
    round: string;
    time: string;
    date?: string;           // Format: "YYYY-MM-DD"
    interviewers: string[];
    status: string;          // "Confirmed" | "Pending"
}

// 5. KEY COMPUTED PROPERTIES
// --------------------------
monthName()          // Returns "January", "February", etc.
calendarDays()       // Returns 42-day grid array
filteredInterviews() // Filters interviews by search query

// 6. HELPER METHODS
// ----------------
formatDateToYYYYMMDD(date: Date): string
// Converts Date object to "2026-02-17" format

toggleCalendarView()
// Switches between 'month' and 'week' view

// 7. SCHEDULING NEW INTERVIEW
// ---------------------------
// When clicking a date's + button:
isScheduleModalOpen.set(true);
newInterview.date = formatDateToYYYYMMDD(calDay.date);

// 8. CALENDAR CELL STYLING
// ------------------------
// Current month days: text-white
// Other month days: text-slate-600
// Today: bg-indigo-500 with rounded-full
// Has interview: Shows colored event pills
// Hover: Shows + button, bg-white/[0.03]

// 9. EVENT DISPLAY LOGIC
// ----------------------
// Each day shows up to 3 interviews
// If more than 3: Shows "+N more" indicator
// Events are color-coded:
//   - Confirmed: bg-indigo-500/20, text-indigo-300
//   - Pending: bg-amber-500/20, text-amber-300

// 10. RESPONSIVE FEATURES
// ----------------------
// - Hover effects on all interactive elements
// - Smooth transitions (transition-all)
// - Scale animations on hover (hover:scale-105)
// - Opacity changes (opacity-0 group-hover:opacity-100)

// USAGE EXAMPLE:
// ==============
/*
1. User clicks "Calendar" tab
2. Calendar loads showing current month (February 2026)
3. User sees 5 interviews distributed across dates
4. User clicks [<] to go to January
5. User clicks "Today" to return to current date
6. User hovers over Feb 19 and clicks [+]
7. Schedule modal opens with date pre-filled
8. User fills in interview details and confirms
9. New interview appears on Feb 19 in the calendar
*/

// CALENDAR GRID STRUCTURE:
// =======================
/*
   Sun   Mon   Tue   Wed   Thu   Fri   Sat
  ┌─────┬─────┬─────┬─────┬─────┬─────┬─────┐
  │  26 │  27 │  28 │  29 │  30 │  31 │   1 │  Week 1 (partial)
  ├─────┼─────┼─────┼─────┼─────┼─────┼─────┤
  │   2 │   3 │   4 │   5 │   6 │   7 │   8 │  Week 2
  ├─────┼─────┼─────┼─────┼─────┼─────┼─────┤
  │   9 │  10 │  11 │  12 │  13 │  14 │  15 │  Week 3
  ├─────┼─────┼─────┼─────┼─────┼─────┼─────┤
  │  16 │ [17]│  18 │  19 │  20 │  21 │  22 │  Week 4 (17 = today)
  ├─────┼─────┼─────┼─────┼─────┼─────┼─────┤
  │  23 │  24 │  25 │  26 │  27 │  28 │   1 │  Week 5
  ├─────┼─────┼─────┼─────┼─────┼─────┼─────┤
  │   2 │   3 │   4 │   5 │   6 │   7 │   8 │  Week 6 (partial)
  └─────┴─────┴─────┴─────┴─────┴─────┴─────┘
  
  Total cells: 42 (6 rows × 7 columns)
  Always shows complete weeks
*/

// COLOR PALETTE:
// =============
const COLORS = {
    background: 'slate-950',
    primary: 'indigo-500',
    success: 'emerald-400',
    warning: 'amber-400',
    error: 'rose-400',
    glass: 'white/5',
    border: 'white/10',
    text: {
        primary: 'white',
        secondary: 'slate-400',
        muted: 'slate-600'
    }
};

// ANIMATION CLASSES:
// =================
// - animate-fade-in: Fade in on mount
// - transition-all: Smooth transitions
// - hover:scale-105: Slight scale on hover
// - hover:bg-white/10: Background change
// - group-hover:opacity-100: Show on parent hover

// ACCESSIBILITY:
// =============
// - Semantic HTML structure
// - ARIA labels on interactive elements
// - Keyboard navigation support
// - Clear visual indicators
// - Sufficient color contrast
