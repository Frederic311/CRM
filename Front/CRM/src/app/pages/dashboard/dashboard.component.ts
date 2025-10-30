import { AfterViewInit, Component, OnInit } from '@angular/core';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { ChartModule } from 'primeng/chart';
import { DropdownModule } from 'primeng/dropdown';
import { TooltipItem } from 'chart.js';
import {TaskService} from "../../services/task/task.service";
import {NgForOf} from "@angular/common";
import { RouterModule } from '@angular/router';


import { CandidateServiceService } from '../../services/candidate/candidate-service.service';
import { EventService } from '../../services/event/event.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MatIconModule, ChartModule, DropdownModule, NgForOf,RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, AfterViewInit {
  basicData: any;
  basicOptions: any;
  chart: any;
  tasks: any[] = [];
  totalCandidates: number = 0;
  totalstudents: number = 0;
  totalEvents: number = 0;

  constructor(
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private taskService: TaskService,
    private CandidateServiceService: CandidateServiceService,
    private eventService: EventService,
  ) {
    this.matIconRegistry.addSvgIcon(
      'tasks',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/list-right-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'user',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/user-plus-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'events',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/calendar-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'students',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/student-cap-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'downarrow',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/arrow-small-down-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'uparrow',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/arrow-small-up-svgrepo-com.svg')
    );
  }

  ngOnInit() {
    this.loadTasks();
    this.initializeChart();
    this.loadCandidates();
    this.calculateWeeklyChange();
    this.calculateYearlyChangeCandidates();
    this.calculateYearlyChangeStudents();
    this.calculateYearlyChangeEvents();
    console.log("basicData", this.basicData);
  }

  ngAfterViewInit() {
    this.updateChart();
  }

  loadCandidates() {
    this.CandidateServiceService.getCandidates().subscribe(candidates => {
        // Initialize counts for each status
        const statusCounts = {
            NEW: 0,
            QUALIFIED: 0,
            NEGOTIATION: 0,
            STUDENT: 0
        };

        candidates.forEach(candidate => {
            switch (candidate.status) {
                case 'NEW':
                    statusCounts.NEW++;
                    break;
                case 'QUALIFIED':
                    statusCounts.QUALIFIED++;
                    break;
                case 'NEGOTIATION':
                    statusCounts.NEGOTIATION++;
                    break;
                case 'STUDENT':
                    statusCounts.STUDENT++;
                    break;
            }
        });

        // Calculate the total number of candidates
        this.totalCandidates = statusCounts.NEW + statusCounts.QUALIFIED + statusCounts.NEGOTIATION + statusCounts.STUDENT;
        this.totalstudents=statusCounts.STUDENT
        // Update the chart data
        this.basicData.datasets[0].data = [
            statusCounts.NEW,
            statusCounts.QUALIFIED,
            statusCounts.NEGOTIATION,
            statusCounts.STUDENT
        ];

        this.updateChart(); // Ensure the chart updates after data is loaded
    });
  }

  initializeChart() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary');
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    this.basicData = {
        labels: ['New', 'Qualified', 'Negotiation', 'Student'],
        datasets: [
            {
                label: 'percentage',
                data: [0, 0, 0, 0], // Initialize with zeros
                backgroundColor: '#CABFE3',
                hoverBackgroundColor: '#2A008D',
                borderWidth: 1,
                borderRadius: 5
            }
        ]
    };

    this.basicOptions = {
        indexAxis: 'y',
        maintainAspectRatio: false,
        aspectRatio: 0.85,
        plugins: {
            legend: {
                display: false
            },
            tooltip: {
                callbacks: {
                    label: (tooltipItem: TooltipItem<'bar'>) => {
                        const value = tooltipItem.raw as number;
                        const total = (tooltipItem.dataset.data as number[]).reduce((sum, item) => sum + (item as number), 0);
                        const percentage = ((value / total) * 100).toFixed(2);
                        return ` ${percentage}%`;
                    }
                }
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    color: textColorSecondary,
                    align: 'start',
                    padding: 5,
                },
                grid: {
                    color: surfaceBorder,
                    drawBorder: false,
                    display: false
                },
                border: {
                    display: false
                }
            },
            x: {
                grid: {
                    color: surfaceBorder,
                    drawBorder: false
                },
                border: {
                    display: false
                },
                ticks: {
                    display: false
                }
            }
        }
    };
  }

  updateChart() {
    if (this.chart) {
        this.chart.update();
    }
  }

  loadTasks(): void {
    this.taskService.getAllTasks().subscribe(
      (data) => {
        this.tasks = data;
      },
      (error) => {
        console.error('Error loading tasks:', error);
      }
    );
  }

  currentWeekCount = 0;
  previousWeekCount = 0;
  percentageChange = 0;

  calculateWeeklyChange(): void {
    this.taskService.getAllTasks().subscribe((tasks) => {
      const now = new Date();
      const currentWeekStart = this.getStartOfWeek(now);
      const previousWeekStart = this.getStartOfWeek(
        new Date(now.setDate(now.getDate() - 7))
      );

      const currentWeekTasks = tasks.filter((task) =>
        this.isDateInRange(
          new Date(task.assignedDate),
          currentWeekStart,
          new Date()
        )
      );

      const previousWeekTasks = tasks.filter((task) =>
        this.isDateInRange(
          new Date(task.assignedDate),
          previousWeekStart,
          new Date(currentWeekStart.setDate(currentWeekStart.getDate() - 1))
        )
      );

      this.currentWeekCount = currentWeekTasks.length;
      this.previousWeekCount = previousWeekTasks.length;

      this.percentageChange = this.calculatePercentageChange(
        this.currentWeekCount,
        this.previousWeekCount
      );
      console.log("percentageChange", this.percentageChange);
    });
  }

  getStartOfWeek(date: Date): Date {
    const day = date.getDay(); // Get the day of the week (0=Sunday, 6=Saturday)
    const diff = date.getDate() - day + (day === 0 ? -6 : 1); // Adjust for ISO week start (Monday)
    return new Date(date.setDate(diff));
  }

  isDateInRange(date: Date, start: Date, end: Date): boolean {
    return date >= start && date <= end;
  }

  calculatePercentageChange(current: number, previous: number): number {
    if (previous === 0) return current === 0 ? 0 : (current === 0 ? -100 : 100);
    const percentageChange = ((current - previous) / previous) * 100;
    return parseFloat(percentageChange.toFixed(2));
  }

  percentageChangeCandidates = 0;
  calculateYearlyChangeCandidates(){
    this.CandidateServiceService.getCandidates().subscribe(candidates => {
      const currentYear = new Date().getFullYear();
      const previousYear = currentYear - 1;
      const currentYearCandidates = candidates.filter((candidate) =>{ 
        const applicationDate = candidate.applicationDate;
        if (!applicationDate) return false;
        return new Date(applicationDate)?.getFullYear() === currentYear
      }).length;

      const previousYearCandidates = candidates.filter((candidate) => {
        const applicationDate = candidate.applicationDate;
        if (!applicationDate) return false;
        return new Date(applicationDate)?.getFullYear() === previousYear
      }).length;

      this.percentageChangeCandidates = this.calculatePercentageChange(
        currentYearCandidates, 
        previousYearCandidates
      );
      
      console.log("currentYearCandidates", currentYearCandidates);
      console.log("previousYearCandidates", previousYearCandidates);
      console.log("percentageChangeCandidates", this.percentageChangeCandidates);
    });
  }

  percentageChangeStudents = 0;

calculateYearlyChangeStudents() {
  this.CandidateServiceService.getCandidates().subscribe(candidates => {
    const currentYear = new Date().getFullYear();
    const previousYear = currentYear - 1;

    // Filtering students from the current year
    const currentYearStudents = candidates.filter((candidate) => {
      const applicationDate = candidate.applicationDate;
      // Ensure applicationDate is valid and the candidate status is 'STUDENT'
      if (!applicationDate || candidate.status !== 'STUDENT') return false;
      return new Date(applicationDate)?.getFullYear() === currentYear;
    }).length;

    // Filtering students from the previous year
    const previousYearStudents = candidates.filter((candidate) => {
      const applicationDate = candidate.applicationDate;
      // Ensure applicationDate is valid and the candidate status is 'STUDENT'
      if (!applicationDate || candidate.status !== 'STUDENT') return false;
      return new Date(applicationDate)?.getFullYear() === previousYear;
    }).length;

    // Calculate the percentage change
    this.percentageChangeStudents = this.calculatePercentageChange(
      currentYearStudents, 
      previousYearStudents
    );

    console.log("currentYearStudents", currentYearStudents);
    console.log("previousYearStudents", previousYearStudents);
    console.log("percentageChangeStudents", this.percentageChangeStudents);
  });
}

percentageChangeEvents = 0;

calculateYearlyChangeEvents() {
  this.eventService.getAllEvents().subscribe(events => {
    this.totalEvents = events.length;
    const currentYear = new Date().getFullYear();
    const previousYear = currentYear - 1;

    // Filtering events from the current year
    const currentYearEvents = events.filter((event) => {
      const startDate = event.start;  // assuming `start` is of type LocalDate
      // Ensure start date is valid
      if (!startDate) return false;
      return new Date(startDate).getFullYear() === currentYear;
    }).length;

    // Filtering events from the previous year
    const previousYearEvents = [...events].filter((event) => {
      const startDate = event.start;  // assuming `start` is of type LocalDate
      // Ensure start date is valid
      if (!startDate) return false;
      return new Date(startDate).getFullYear() === previousYear;
    }).length;

    // Calculate the percentage change
    this.percentageChangeEvents = this.calculatePercentageChange(
      currentYearEvents, 
      previousYearEvents
    );

    console.log("currentYearEvents", currentYearEvents);
    console.log("previousYearEvents", previousYearEvents);
    console.log("percentageChangeEvents", this.percentageChangeEvents);
  });
}

  allTasksTheseMonth(){
    if (this.tasks.length===0) return [];
    return [...this.tasks].filter((task) => {
      const taskDate = new Date(task.assignedDate);
      return taskDate.getMonth() === new Date().getMonth();
    });
  }

  getStatusDistribution(status: string): any{
    const status_task = [...this.tasks].filter(t => t.status === status);
    return {
      width: (status_task.length/this.tasks.length)*100+'%',
      number: status_task.length
    };
  }

  getPriorityColor(priority: string): string {
    switch (priority) {
      case 'HIGH':
        return 'red';
      case 'MEDIUM':
        return 'orange';
      case 'LOW':
        return 'blue';
      default:
        return 'black'; // Default color
    }
  }

  getMostRecentTasks(){
    const mostRecentTasks = [...this.tasks]
    .sort((a, b) => new Date(b.assignedDate).getTime() - new Date(a.assignedDate).getTime())
    .slice(0, 3);
    return mostRecentTasks;
  }
  
}
