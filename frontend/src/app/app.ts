import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  LucideAngularModule,
  Calendar,
  Users,
  CheckCircle,
  Clock,
  ArrowRight,
  Plus,
  Filter,
  BarChart3,
  Search,
  ChevronRight,
  ChevronLeft,
  ChevronDown,
  UserCheck,
  AlertCircle,
  X,
  Mail,
  TrendingUp,
  FileText,
  Trash2,
  RefreshCw
} from 'lucide-angular';

interface Interview {
  id: number;
  candidate: string;
  round: string;
  time: string;
  date?: string;
  interviewers: string[];
  status: string;
}

interface CalendarDay {
  date: Date;
  day: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  interviews: Interview[];
}

interface Candidate {
  id: number;
  name: string;
  pos: string;
  stage: string;
  status: string;
  email: string;
  remarks?: string;
}

interface Interviewer {
  name: string;
  role: string;
  load: number;
  skills: string[];
  rating: number;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  // Icons
  readonly CalendarIcon = Calendar;
  readonly UsersIcon = Users;
  readonly CheckCircleIcon = CheckCircle;
  readonly ClockIcon = Clock;
  readonly ArrowRightIcon = ArrowRight;
  readonly PlusIcon = Plus;
  readonly FilterIcon = Filter;
  readonly BarChart3Icon = BarChart3;
  readonly SearchIcon = Search;
  readonly ChevronRightIcon = ChevronRight;
  readonly ChevronLeftIcon = ChevronLeft;
  readonly ChevronDownIcon = ChevronDown;
  readonly UserCheckIcon = UserCheck;
  readonly AlertCircleIcon = AlertCircle;
  readonly XIcon = X;
  readonly MailIcon = Mail;
  readonly TrendingUpIcon = TrendingUp;
  readonly FileTextIcon = FileText;
  readonly TrashIcon = Trash2;
  readonly RefreshIcon = RefreshCw;

  // State
  activeTab = signal('dashboard');
  searchQuery = signal('');
  toast = signal<{ message: string, type: 'success' | 'alert' } | null>(null);
  isScheduleModalOpen = signal(false);

  // Calendar State
  currentMonth = signal(new Date().getMonth());
  currentYear = signal(new Date().getFullYear());
  calendarView = signal<'month' | 'week'>('month');

  // Candidate Modals
  isCandidateModalOpen = signal(false);
  isUpdateStatusModalOpen = signal(false);
  editingCandidate = signal<(Candidate & { nextRoundDate?: string, nextRoundTime?: string }) | null>(null);

  newCandidate = {
    name: '',
    pos: 'Senior Backend',
    stage: 'Screening',
    status: 'Applied',
    email: '',
    remarks: ''
  };

  // Data
  interviews = signal<Interview[]>([
    { id: 1, candidate: "Rahul Sharma", round: "Technical Round 1", time: "10:00 AM - 11:30 AM", date: "2026-02-18", interviewers: ['Siddharth', 'Isha'], status: "Confirmed" },
    { id: 2, candidate: "Sneha Kapoor", round: "System Design", time: "1:00 PM - 2:30 PM", date: "2026-02-20", interviewers: ['Kavita', 'Abhishek'], status: "Confirmed" },
    { id: 3, candidate: "Vikram Singh", round: "Hiring Manager", time: "4:00 PM - 5:00 PM", date: "2026-02-22", interviewers: ['Manish'], status: "Pending" },
    { id: 4, candidate: "Ananya Reddy", round: "Technical Round 2", time: "2:00 PM - 3:30 PM", date: "2026-02-25", interviewers: ['Siddharth', 'Abhishek'], status: "Confirmed" },
    { id: 5, candidate: "Karthik Menon", round: "Behavioral", time: "11:00 AM - 12:00 PM", date: "2026-02-27", interviewers: ['Kavita'], status: "Pending" }
  ]);

  candidates = signal<Candidate[]>([
    { id: 1, name: 'Aditi Verma', pos: 'Senior Backend', stage: 'Round 1', status: 'In Review', email: 'aditi.v@example.com', remarks: 'Strong background in Java' },
    { id: 2, name: 'Amit Patel', pos: 'Product Manager', stage: 'Screening', status: 'Applied', email: 'amit.p@example.com', remarks: 'Needs architectural review' },
    { id: 3, name: 'Priya Dass', pos: 'UI/UX Designer', stage: 'Final', status: 'High Intent', email: 'priya.d@example.com', remarks: 'Portfolio looks great' },
    { id: 4, name: 'Rohan Gupta', pos: 'Senior Backend', stage: 'Round 2', status: 'Scheduling', email: 'rohan.g@example.com', remarks: 'Excellent problem solver' }
  ]);

  availableInterviewers = signal<Interviewer[]>([
    { name: 'Siddharth Malhotra', role: 'Staff Software Engineer', load: 12, skills: ['Java', 'System Design', 'K8s'], rating: 4.8 },
    { name: 'Isha Agrawal', role: 'Frontend Architect', load: 8, skills: ['React', 'TypeScript', 'UI/UX'], rating: 4.9 },
    { name: 'Kavita Reddy', role: 'Engineering Manager', load: 4, skills: ['Leadership', 'Behavioral'], rating: 5.0 },
    { name: 'Abhishek Jain', role: 'Senior SDE', load: 10, skills: ['Java', 'SQL', 'AWS'], rating: 4.7 },
    { name: 'Manish Pandey', role: 'Senior SDE', load: 6, skills: ['Python', 'Docker', 'Go'], rating: 4.6 }
  ]);

  // Form State
  newInterview = {
    candidate: '',
    pos: 'Senior Backend Engineer',
    round: 'Technical Round 1',
    date: '',
    time: '',
    selectedInterviewers: [] as string[]
  };

  // Computed
  filteredInterviews = computed(() => {
    const query = this.searchQuery().toLowerCase();
    return this.interviews().filter(i =>
      i.candidate.toLowerCase().includes(query) ||
      i.round.toLowerCase().includes(query)
    );
  });

  filteredCandidates = computed(() => {
    const query = this.searchQuery().toLowerCase();
    return this.candidates().filter(c =>
      c.name.toLowerCase().includes(query) ||
      c.pos.toLowerCase().includes(query)
    );
  });

  filteredInterviewers = computed(() => {
    const query = this.searchQuery().toLowerCase();
    return this.availableInterviewers().filter(i =>
      i.name.toLowerCase().includes(query) ||
      i.role.toLowerCase().includes(query) ||
      i.skills.some(s => s.toLowerCase().includes(query))
    );
  });

  // Actions
  showToast(message: string, type: 'success' | 'alert' = 'success') {
    this.toast.set({ message, type });
    setTimeout(() => this.toast.set(null), 3000);
  }

  handleTabChange(tab: string) {
    this.activeTab.set(tab);
  }

  toggleInterviewer(name: string) {
    const idx = this.newInterview.selectedInterviewers.indexOf(name);
    if (idx > -1) {
      this.newInterview.selectedInterviewers.splice(idx, 1);
    } else {
      this.newInterview.selectedInterviewers.push(name);
    }
  }

  confirmSchedule() {
    if (!this.newInterview.candidate) {
      this.showToast('Please enter a candidate name', 'alert');
      return;
    }

    const nextId = this.interviews().length + 1;
    const newEntry: Interview = {
      id: nextId,
      candidate: this.newInterview.candidate,
      round: this.newInterview.round,
      time: `${this.newInterview.date} ${this.newInterview.time}`,
      interviewers: [...this.newInterview.selectedInterviewers],
      status: 'Confirmed'
    };

    this.interviews.update(prev => [newEntry, ...prev]);
    this.isScheduleModalOpen.set(false);
    this.showToast(`Interview scheduled for ${newEntry.candidate}!`);

    // Reset form
    this.newInterview = {
      candidate: '',
      pos: 'Senior Backend Engineer',
      round: 'Technical Round 1',
      date: '',
      time: '',
      selectedInterviewers: []
    };
  }

  openFullCalendar() {
    this.activeTab.set('calendar');
    this.showToast('Opening Full Calendar View...');
  }

  deleteInterview(id: number) {
    this.interviews.update(prev => prev.filter(i => i.id !== id));
    this.showToast('Interview deleted successfully', 'alert');
  }

  syncWithOutlook() {
    this.showToast('Syncing with Outlook Calendar...');
    setTimeout(() => {
      this.showToast('Outlook Calendar synchronized! All conflicts resolved.', 'success');
    }, 2000);
  }

  // Candidate Actions
  addCandidate() {
    if (!this.newCandidate.name || !this.newCandidate.email) {
      this.showToast('Please fill in name and email', 'alert');
      return;
    }
    const nextId = Math.max(...this.candidates().map(c => c.id), 0) + 1;
    const candidate: Candidate = {
      ...this.newCandidate,
      id: nextId
    };
    this.candidates.update(prev => [candidate, ...prev]);
    this.isCandidateModalOpen.set(false);
    this.showToast(`Candidate ${candidate.name} added!`);
    this.newCandidate = { name: '', pos: 'Senior Backend', stage: 'Screening', status: 'Applied', email: '', remarks: '' };
  }

  openUpdateStatus(candidate: Candidate) {
    this.editingCandidate.set({ ...candidate });
    this.isUpdateStatusModalOpen.set(true);
  }

  updateCandidateStatus() {
    const candidate = this.editingCandidate();
    if (!candidate) return;

    // Check if progress is made (e.g., status includes "Cleared")
    if ((candidate.status.toLowerCase().includes('cleared') || candidate.status.toLowerCase().includes('high intent')) && candidate.nextRoundDate) {
      const nextId = this.interviews().length + 1;
      const nextRound = this.getNextRoundName(candidate.stage);

      const newEntry: Interview = {
        id: nextId,
        candidate: candidate.name,
        round: nextRound,
        time: candidate.nextRoundTime ? `${candidate.nextRoundTime}` : '10:00 AM',
        date: candidate.nextRoundDate,
        interviewers: ['Siddharth Malhotra'], // Default panel
        status: 'Confirmed'
      };

      this.interviews.update(prev => [newEntry, ...prev]);
      this.showToast(`Next round scheduled for ${candidate.name}!`);

      // Update candidate stage to next round
      candidate.stage = nextRound;
      candidate.status = 'Scheduling';
    }

    this.candidates.update(prev => prev.map(c => c.id === candidate.id ? candidate : c));
    this.isUpdateStatusModalOpen.set(false);
    this.showToast(`Status updated for ${candidate.name}`);
  }

  private getNextRoundName(currentStage: string): string {
    const rounds = ['Screening', 'Round 1', 'Round 2', 'Final'];
    const idx = rounds.indexOf(currentStage);
    return (idx !== -1 && idx < rounds.length - 1) ? rounds[idx + 1] : 'Final Review';
  }

  deleteCandidate(id: number) {
    this.candidates.update(prev => prev.filter(c => c.id !== id));
    this.showToast('Candidate removed', 'alert');
  }

  // Calendar Methods
  calendarDays = computed(() => {
    const year = this.currentYear();
    const month = this.currentMonth();
    const view = this.calendarView();

    const days: CalendarDay[] = [];
    const today = new Date();

    if (view === 'month') {
      const firstDay = new Date(year, month, 1);
      const lastDay = new Date(year, month + 1, 0);
      const prevLastDay = new Date(year, month, 0);

      const firstDayOfWeek = firstDay.getDay();
      const daysInMonth = lastDay.getDate();
      const daysInPrevMonth = prevLastDay.getDate();

      // Previous month days
      for (let i = firstDayOfWeek - 1; i >= 0; i--) {
        const date = new Date(year, month - 1, daysInPrevMonth - i);
        days.push({
          date,
          day: daysInPrevMonth - i,
          isCurrentMonth: false,
          isToday: false,
          interviews: []
        });
      }

      // Current month days
      for (let i = 1; i <= daysInMonth; i++) {
        const date = new Date(year, month, i);
        const dateStr = this.formatDateToYYYYMMDD(date);
        const dayInterviews = this.interviews().filter(interview => interview.date === dateStr);

        days.push({
          date,
          day: i,
          isCurrentMonth: true,
          isToday: date.toDateString() === today.toDateString(),
          interviews: dayInterviews
        });
      }

      // Next month days
      const remainingDays = 42 - days.length; // 6 rows * 7 days
      for (let i = 1; i <= remainingDays; i++) {
        const date = new Date(year, month + 1, i);
        days.push({
          date,
          day: i,
          isCurrentMonth: false,
          isToday: false,
          interviews: []
        });
      }
    } else {
      // Week View: Show current week starting from Sunday
      const curr = new Date(year, month, today.getDate());
      const first = curr.getDate() - curr.getDay();

      for (let i = 0; i < 7; i++) {
        const date = new Date(year, month, first + i);
        const dateStr = this.formatDateToYYYYMMDD(date);
        const dayInterviews = this.interviews().filter(interview => interview.date === dateStr);

        days.push({
          date,
          day: date.getDate(),
          isCurrentMonth: date.getMonth() === month,
          isToday: date.toDateString() === today.toDateString(),
          interviews: dayInterviews
        });
      }
    }

    return days;
  });

  monthName = computed(() => {
    const months = ['January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'];
    return months[this.currentMonth()];
  });

  previousMonth() {
    if (this.currentMonth() === 0) {
      this.currentMonth.set(11);
      this.currentYear.update(y => y - 1);
    } else {
      this.currentMonth.update(m => m - 1);
    }
  }

  nextMonth() {
    if (this.currentMonth() === 11) {
      this.currentMonth.set(0);
      this.currentYear.update(y => y + 1);
    } else {
      this.currentMonth.update(m => m + 1);
    }
  }

  goToToday() {
    const today = new Date();
    this.currentMonth.set(today.getMonth());
    this.currentYear.set(today.getFullYear());
  }

  formatDateToYYYYMMDD(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  toggleCalendarView() {
    this.calendarView.update(v => v === 'month' ? 'week' : 'month');
  }
}
