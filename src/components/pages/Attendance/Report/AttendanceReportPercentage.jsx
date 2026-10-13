import { useState, useMemo, useCallback } from "react";
import {
  BarChart2,
  ChevronDown,
  Search,
  Filter,
  Download,
  Eye,
  Send,
  X,
  CheckCircle,
  AlertCircle,
  Users,
  TrendingUp,
  TrendingDown,
  BookOpen,
  Percent,
  RefreshCw,
  ChevronRight,
  Home,
  MessageSquare,
  SlidersHorizontal,
} from "lucide-react";

// ─── Static / Dummy Data ────────────────────────────────────────────────────

const SESSIONS = [
  { value: "0", label: "Select Session" },
  { value: "2024-25", label: "2024-25" },
  { value: "2023-24", label: "2023-24" },
  { value: "2022-23", label: "2022-23" },
];

const CLASSES = [
  { value: "1", label: "Class I" },
  { value: "2", label: "Class II" },
  { value: "3", label: "Class III" },
  { value: "4", label: "Class IV" },
  { value: "5", label: "Class V" },
  { value: "6", label: "Class VI" },
  { value: "7", label: "Class VII" },
  { value: "8", label: "Class VIII" },
  { value: "9", label: "Class IX" },
  { value: "10", label: "Class X" },
];

const SECTIONS_MAP = {
  "1": ["A", "B", "C"],
  "2": ["A", "B"],
  "3": ["A", "B", "C", "D"],
  "4": ["A", "B"],
  "5": ["A", "B", "C"],
  "6": ["A", "B"],
  "7": ["A", "B", "C"],
  "8": ["A", "B"],
  "9": ["A", "B", "C"],
  "10": ["A", "B"],
};

const generateStudents = () => {
  const names = [
    "Aarav Sharma", "Priya Singh", "Rohan Verma", "Ananya Gupta",
    "Vivaan Patel", "Ishaan Mehta", "Kavya Reddy", "Arjun Kumar",
    "Nisha Joshi", "Dev Agarwal", "Sneha Rao", "Karan Tiwari",
    "Pooja Nair", "Rishi Malhotra", "Divya Krishnan", "Amit Bajaj",
    "Shruti Pandey", "Nikhil Saxena", "Meera Iyer", "Suresh Dubey",
    "Deepika Mishra", "Abhay Chaudhary", "Tanvi Pillai", "Rahul Bose",
    "Simran Kapoor", "Varun Sharma", "Aditi Desai", "Mohit Srivastava",
    "Pallavi Jain", "Siddharth Yadav",
  ];
  return names.map((name, i) => ({
    id: i + 1,
    admNo: `ADM${(2024001 + i).toString()}`,
    name,
    class: CLASSES[i % CLASSES.length].label,
    section: ["A", "B", "C"][i % 3],
    percentage: Math.floor(60 + Math.random() * 40),
    present: Math.floor(150 + Math.random() * 50),
    total: 210,
    phone: `98${Math.floor(10000000 + Math.random() * 89999999)}`,
  }));
};

const ALL_STUDENTS = generateStudents();

// ─── Sub-components ─────────────────────────────────────────────────────────

/** Breadcrumb */
const Breadcrumb = () => (
  <nav className="flex items-center gap-1.5 text-sm text-gray-500 mb-6 flex-wrap">
    <Home size={14} className="text-gray-400" />
    <span className="hover:text-emerald-600 cursor-pointer transition-colors">Home</span>
    <ChevronRight size={13} className="text-gray-400" />
    <span className="text-gray-700 font-medium">Attendance Report (%)</span>
  </nav>
);

/** Stat card */
const StatCard = ({ icon: Icon, label, value, color }) => (
  <div className={`bg-white rounded-xl border border-gray-100 p-4 flex items-center gap-3 shadow-sm`}>
    <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${color}`}>
      <Icon size={18} className="text-white" />
    </div>
    <div>
      <p className="text-xs text-gray-500 font-medium">{label}</p>
      <p className="text-xl font-bold text-gray-800">{value}</p>
    </div>
  </div>
);

/** Percentage badge */
const PctBadge = ({ pct }) => {
  const p = Number(pct);
  const cls =
    p >= 90
      ? "bg-emerald-50 text-emerald-700 border border-emerald-200"
      : p >= 75
      ? "bg-blue-50 text-blue-700 border border-blue-200"
      : p >= 60
      ? "bg-amber-50 text-amber-700 border border-amber-200"
      : "bg-red-50 text-red-700 border border-red-200";
  return (
    <span className={`inline-flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full ${cls}`}>
      <Percent size={11} />
      {p}%
    </span>
  );
};

/** Mobile Student Card */
const StudentCard = ({ student, onSendSms }) => {
  const [expanded, setExpanded] = useState(false);
  const [msg, setMsg] = useState("");
  return (
    <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
      <div
        className="flex items-center justify-between p-4 cursor-pointer"
        onClick={() => setExpanded((v) => !v)}
      >
        <div className="flex items-center gap-3 min-w-0">
          <div className="w-9 h-9 rounded-full bg-emerald-100 flex items-center justify-center flex-shrink-0">
            <span className="text-emerald-700 font-bold text-sm">
              {student.name.charAt(0)}
            </span>
          </div>
          <div className="min-w-0">
            <p className="font-semibold text-gray-800 text-sm truncate">{student.name}</p>
            <p className="text-xs text-gray-500">{student.admNo} · {student.class} - {student.section}</p>
          </div>
        </div>
        <div className="flex items-center gap-2 flex-shrink-0">
          <PctBadge pct={student.percentage} />
          <ChevronDown
            size={15}
            className={`text-gray-400 transition-transform ${expanded ? "rotate-180" : ""}`}
          />
        </div>
      </div>

      {expanded && (
        <div className="border-t border-gray-100 bg-gray-50 px-4 py-3 space-y-3">
          <div className="grid grid-cols-3 gap-2 text-center text-xs">
            <div className="bg-white rounded-lg p-2 border border-gray-100">
              <p className="text-gray-500">Present</p>
              <p className="font-bold text-gray-800">{student.present}</p>
            </div>
            <div className="bg-white rounded-lg p-2 border border-gray-100">
              <p className="text-gray-500">Total</p>
              <p className="font-bold text-gray-800">{student.total}</p>
            </div>
            <div className="bg-white rounded-lg p-2 border border-gray-100">
              <p className="text-gray-500">Absent</p>
              <p className="font-bold text-red-600">{student.total - student.present}</p>
            </div>
          </div>
          {/* SMS section */}
          <div className="flex gap-2">
            <textarea
              value={msg}
              onChange={(e) => setMsg(e.target.value)}
              placeholder="Type SMS message..."
              rows={2}
              className="flex-1 text-xs border border-gray-200 rounded-lg p-2 resize-none focus:outline-none focus:ring-2 focus:ring-emerald-400 bg-white"
            />
            <button
              onClick={() => onSendSms(student, msg)}
              className="flex-shrink-0 bg-emerald-600 hover:bg-emerald-700 active:scale-95 text-white rounded-lg px-3 flex items-center gap-1 text-xs font-semibold transition-all"
            >
              <Send size={13} /> SMS
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

/** Multi-select pill list */
const MultiSelectList = ({ label, options, selected, onChange }) => {
  const toggle = (val) => {
    onChange(
      selected.includes(val) ? selected.filter((v) => v !== val) : [...selected, val]
    );
  };
  return (
    <div>
      <label className="block text-xs font-semibold text-gray-600 mb-1.5 uppercase tracking-wide">
        {label}
      </label>
      <div className="border border-gray-200 rounded-xl bg-white overflow-hidden">
        <div className="flex flex-wrap gap-1.5 p-2.5 max-h-32 overflow-y-auto">
          {options.length === 0 ? (
            <span className="text-xs text-gray-400 px-1">Select session first</span>
          ) : (
            options.map((opt) => (
              <button
                key={opt.value}
                onClick={() => toggle(opt.value)}
                className={`text-xs px-3 py-1.5 rounded-full font-medium transition-all border ${
                  selected.includes(opt.value)
                    ? "bg-emerald-600 text-white border-emerald-600"
                    : "bg-gray-50 text-gray-700 border-gray-200 hover:border-emerald-400"
                }`}
              >
                {opt.label}
              </button>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

// ─── Toast notification ──────────────────────────────────────────────────────
const Toast = ({ msg, onClose }) => (
  <div className="fixed bottom-5 right-5 z-50 flex items-center gap-3 bg-gray-900 text-white text-sm px-4 py-3 rounded-xl shadow-xl animate-fade-in">
    <CheckCircle size={16} className="text-emerald-400 flex-shrink-0" />
    <span>{msg}</span>
    <button onClick={onClose} className="text-gray-400 hover:text-white ml-2">
      <X size={15} />
    </button>
  </div>
);

// ─── Main Component ──────────────────────────────────────────────────────────

export default function AttendanceReportPercentage() {
  // Form state
  const [session, setSession] = useState("0");
  const [selectedClasses, setSelectedClasses] = useState([]);
  const [selectedSections, setSelectedSections] = useState([]);
  const [fromPct, setFromPct] = useState("");
  const [toPct, setToPct] = useState("");
  const [errors, setErrors] = useState({});

  // UI state
  const [activeTab, setActiveTab] = useState("show"); // show | export
  const [reportData, setReportData] = useState([]);
  const [showReport, setShowReport] = useState(false);
  const [showFiltersDrawer, setShowFiltersDrawer] = useState(false);
  const [search, setSearch] = useState("");
  const [sortKey, setSortKey] = useState("name");
  const [sortDir, setSortDir] = useState("asc");
  const [toast, setToast] = useState(null);
  const [smsMsg, setSmsMsg] = useState({});

  // Available sections based on selected classes
  const availableSections = useMemo(() => {
    const all = new Set();
    selectedClasses.forEach((c) => {
      (SECTIONS_MAP[c] || []).forEach((s) => all.add(s));
    });
    return [...all].map((s) => ({ value: s, label: `Section ${s}` }));
  }, [selectedClasses]);

  // Validation
  const validate = useCallback(() => {
    const errs = {};
    if (session === "0") errs.session = "Please select a session";
    if (selectedClasses.length === 0) errs.classes = "Select at least one class";
    if (fromPct && toPct && Number(fromPct) > Number(toPct))
      errs.pct = "From % cannot be greater than To %";
    setErrors(errs);
    return Object.keys(errs).length === 0;
  }, [session, selectedClasses, fromPct, toPct]);

  // Show report
  const handleShow = useCallback(() => {
    if (!validate()) return;
    let data = ALL_STUDENTS.filter((s) => {
      const classMatch =
        selectedClasses.length === 0 ||
        selectedClasses.some((c) => CLASSES.find((cl) => cl.value === c)?.label === s.class);
      const sectionMatch =
        selectedSections.length === 0 || selectedSections.includes(s.section);
      const pctMatch =
        (!fromPct || s.percentage >= Number(fromPct)) &&
        (!toPct || s.percentage <= Number(toPct));
      return classMatch && sectionMatch && pctMatch;
    });
    setReportData(data);
    setShowReport(true);
    setSearch("");
    setShowFiltersDrawer(false);
  }, [validate, selectedClasses, selectedSections, fromPct, toPct]);

  // Export CSV
  const handleExport = useCallback(() => {
    if (!validate()) return;
    const headers = ["S.No", "Adm No", "Student Name", "Class", "Section", "Percentage", "Present", "Total"];
    const rows = reportData.map((s, i) =>
      [i + 1, s.admNo, s.name, s.class, s.section, `${s.percentage}%`, s.present, s.total].join(",")
    );
    const csv = [headers.join(","), ...rows].join("\n");
    const blob = new Blob([csv], { type: "text/csv" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `attendance_report_${session}.csv`;
    a.click();
    showToast("Report exported successfully!");
  }, [validate, reportData, session]);

  // Send SMS
  const handleSendSms = useCallback((student, msg) => {
    if (!msg.trim()) {
      showToast("Please enter a message first.");
      return;
    }
    showToast(`SMS sent to ${student.name} (${student.phone})`);
  }, []);

  const showToast = (msg) => {
    setToast(msg);
    setTimeout(() => setToast(null), 3500);
  };

  // Sorting
  const handleSort = (key) => {
    if (sortKey === key) setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    else { setSortKey(key); setSortDir("asc"); }
  };

  // Filtered + sorted data
  const filteredData = useMemo(() => {
    let data = reportData.filter(
      (s) =>
        s.name.toLowerCase().includes(search.toLowerCase()) ||
        s.admNo.toLowerCase().includes(search.toLowerCase()) ||
        s.class.toLowerCase().includes(search.toLowerCase())
    );
    data.sort((a, b) => {
      let av = a[sortKey], bv = b[sortKey];
      if (typeof av === "string") av = av.toLowerCase();
      if (typeof bv === "string") bv = bv.toLowerCase();
      if (av < bv) return sortDir === "asc" ? -1 : 1;
      if (av > bv) return sortDir === "asc" ? 1 : -1;
      return 0;
    });
    return data;
  }, [reportData, search, sortKey, sortDir]);

  // Stats
  const stats = useMemo(() => {
    if (!filteredData.length) return null;
    const avg = Math.round(filteredData.reduce((s, r) => s + r.percentage, 0) / filteredData.length);
    const above90 = filteredData.filter((r) => r.percentage >= 90).length;
    const below75 = filteredData.filter((r) => r.percentage < 75).length;
    return { total: filteredData.length, avg, above90, below75 };
  }, [filteredData]);

  const SortIcon = ({ k }) => (
    <span className="ml-1 text-gray-400">
      {sortKey === k ? (sortDir === "asc" ? "↑" : "↓") : "↕"}
    </span>
  );

  // ── Filter Panel (shared for desktop sidebar & mobile drawer) ──
  const FilterPanel = () => (
    <div className="space-y-4">
      {/* Session */}
      <div>
        <label className="block text-xs font-semibold text-gray-600 mb-1.5 uppercase tracking-wide">
          Session
        </label>
        <select
          value={session}
          onChange={(e) => { setSession(e.target.value); setSelectedClasses([]); setSelectedSections([]); }}
          className={`w-full border rounded-xl text-sm px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-emerald-400 bg-white transition ${
            errors.session ? "border-red-400" : "border-gray-200"
          }`}
        >
          {SESSIONS.map((s) => (
            <option key={s.value} value={s.value}>{s.label}</option>
          ))}
        </select>
        {errors.session && <p className="text-xs text-red-500 mt-1">{errors.session}</p>}
      </div>

      {/* Classes */}
      <div>
        <MultiSelectList
          label="Class"
          options={session !== "0" ? CLASSES : []}
          selected={selectedClasses}
          onChange={setSelectedClasses}
        />
        {errors.classes && <p className="text-xs text-red-500 mt-1">{errors.classes}</p>}
      </div>

      {/* Sections */}
      <MultiSelectList
        label="Section"
        options={availableSections}
        selected={selectedSections}
        onChange={setSelectedSections}
      />

      {/* Percentage Range */}
      <div>
        <label className="block text-xs font-semibold text-gray-600 mb-1.5 uppercase tracking-wide">
          Attendance % Range
        </label>
        <div className="flex gap-2 items-center">
          <input
            type="number"
            min="0"
            max="100"
            value={fromPct}
            onChange={(e) => setFromPct(e.target.value)}
            placeholder="From"
            className="w-full border border-gray-200 rounded-xl text-sm px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-emerald-400 bg-white"
          />
          <span className="text-gray-400 font-bold">–</span>
          <input
            type="number"
            min="0"
            max="100"
            value={toPct}
            onChange={(e) => setToPct(e.target.value)}
            placeholder="To"
            className="w-full border border-gray-200 rounded-xl text-sm px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-emerald-400 bg-white"
          />
        </div>
        {errors.pct && <p className="text-xs text-red-500 mt-1">{errors.pct}</p>}
      </div>

      {/* Action buttons */}
      <div className="flex gap-2 pt-2">
        <button
          onClick={handleShow}
          className="flex-1 flex items-center justify-center gap-1.5 bg-emerald-600 hover:bg-emerald-700 active:scale-95 text-white text-sm font-semibold py-2.5 rounded-xl transition-all"
        >
          <Eye size={15} /> Show Report
        </button>
        <button
          onClick={handleExport}
          className="flex-1 flex items-center justify-center gap-1.5 bg-white hover:bg-gray-50 active:scale-95 text-gray-700 text-sm font-semibold py-2.5 rounded-xl border border-gray-200 transition-all"
        >
          <Download size={15} /> Export
        </button>
      </div>

      <button
        onClick={() => {
          setSession("0"); setSelectedClasses([]); setSelectedSections([]);
          setFromPct(""); setToPct(""); setErrors({}); setShowReport(false); setSearch("");
        }}
        className="w-full flex items-center justify-center gap-1.5 text-xs text-gray-500 hover:text-gray-700 py-2 transition-colors"
      >
        <RefreshCw size={12} /> Reset All Filters
      </button>
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50/80">
      {/* ── Header ── */}
      <div className="bg-white border-b border-gray-100 shadow-sm sticky top-0 z-30">
        <div className="max-w-screen-xl mx-auto px-4 sm:px-6 py-3.5 flex items-center justify-between gap-3">
          <div className="flex items-center gap-3 min-w-0">
            <div className="w-9 h-9 bg-emerald-600 rounded-xl flex items-center justify-center flex-shrink-0">
              <BarChart2 size={18} className="text-white" />
            </div>
            <div className="min-w-0">
              <h1 className="text-base font-bold text-gray-900 truncate">
                Attendance Report
              </h1>
              <p className="text-xs text-gray-500 hidden sm:block">
                Student-wise attendance percentage
              </p>
            </div>
          </div>

          {/* Mobile filter trigger */}
          <button
            onClick={() => setShowFiltersDrawer(true)}
            className="lg:hidden flex items-center gap-1.5 text-sm font-medium text-emerald-700 bg-emerald-50 border border-emerald-200 px-3 py-2 rounded-xl"
          >
            <SlidersHorizontal size={14} /> Filters
          </button>
        </div>
      </div>

      <div className="max-w-screen-xl mx-auto px-4 sm:px-6 py-6">
        <Breadcrumb />

        <div className="flex gap-6 items-start">
          {/* ── Desktop Sidebar ── */}
          <aside className="hidden lg:block w-72 flex-shrink-0">
            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 sticky top-20">
              <div className="flex items-center gap-2 mb-5">
                <Filter size={15} className="text-emerald-600" />
                <h2 className="text-sm font-bold text-gray-800">Report Filters</h2>
              </div>
              <FilterPanel />
            </div>
          </aside>

          {/* ── Main Content ── */}
          <main className="flex-1 min-w-0">
            {/* Stats row */}
            {showReport && stats && (
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-5">
                <StatCard icon={Users} label="Total Students" value={stats.total} color="bg-emerald-500" />
                <StatCard icon={Percent} label="Avg Attendance" value={`${stats.avg}%`} color="bg-blue-500" />
                <StatCard icon={TrendingUp} label="Above 90%" value={stats.above90} color="bg-violet-500" />
                <StatCard icon={TrendingDown} label="Below 75%" value={stats.below75} color="bg-red-500" />
              </div>
            )}

            {/* Report box */}
            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
              {/* Report header */}
              <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100 flex-wrap gap-3">
                <div>
                  <h2 className="text-sm font-bold text-gray-800">
                    {showReport ? `Report Results` : "100% Attendance Report"}
                  </h2>
                  {showReport && (
                    <p className="text-xs text-gray-500 mt-0.5">
                      Session: <span className="font-medium text-gray-700">{session}</span>
                      {" · "}
                      {filteredData.length} records
                    </p>
                  )}
                </div>

                {showReport && (
                  <div className="relative w-full sm:w-64">
                    <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input
                      type="text"
                      value={search}
                      onChange={(e) => setSearch(e.target.value)}
                      placeholder="Search name, adm no, class..."
                      className="w-full pl-9 pr-3 py-2 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-400 bg-gray-50"
                    />
                  </div>
                )}
              </div>

              {/* Empty/Initial state */}
              {!showReport && (
                <div className="flex flex-col items-center justify-center py-20 px-6 text-center">
                  <div className="w-16 h-16 bg-emerald-50 rounded-2xl flex items-center justify-center mb-4">
                    <BarChart2 size={28} className="text-emerald-500" />
                  </div>
                  <h3 className="text-base font-semibold text-gray-700 mb-1">
                    Configure & Generate Report
                  </h3>
                  <p className="text-sm text-gray-500 max-w-xs">
                    Use the filter panel to select session, class, section and percentage range, then click <strong>Show Report</strong>.
                  </p>
                </div>
              )}

              {/* Desktop Table */}
              {showReport && (
                <>
                  {/* Mobile cards */}
                  <div className="block lg:hidden divide-y divide-gray-100">
                    {filteredData.length === 0 ? (
                      <div className="py-12 text-center text-sm text-gray-500">
                        <AlertCircle size={24} className="mx-auto mb-2 text-gray-300" />
                        No records found.
                      </div>
                    ) : (
                      <div className="p-4 space-y-3">
                        {filteredData.map((student) => (
                          <StudentCard key={student.id} student={student} onSendSms={handleSendSms} />
                        ))}
                      </div>
                    )}
                  </div>

                  {/* Desktop Table */}
                  <div className="hidden lg:block overflow-x-auto">
                    <table className="w-full text-sm">
                      <thead>
                        <tr className="bg-gray-50 border-b border-gray-100">
                          {[
                            { key: "id", label: "S.No." },
                            { key: "admNo", label: "Adm No." },
                            { key: "name", label: "Student Name" },
                            { key: "class", label: "Class" },
                            { key: "section", label: "Section" },
                            { key: "percentage", label: "Attendance %" },
                          ].map((col) => (
                            <th
                              key={col.key}
                              onClick={() => handleSort(col.key)}
                              className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wide cursor-pointer hover:bg-gray-100 select-none whitespace-nowrap transition-colors"
                            >
                              {col.label}
                              <SortIcon k={col.key} />
                            </th>
                          ))}
                          <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wide">
                            Send SMS
                          </th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-gray-50">
                        {filteredData.length === 0 ? (
                          <tr>
                            <td colSpan={7} className="py-14 text-center text-sm text-gray-500">
                              <AlertCircle size={24} className="mx-auto mb-2 text-gray-300" />
                              No records found for selected criteria.
                            </td>
                          </tr>
                        ) : (
                          filteredData.map((student, idx) => (
                            <tr
                              key={student.id}
                              className={`hover:bg-emerald-50/30 transition-colors ${idx % 2 === 0 ? "bg-white" : "bg-gray-50/40"}`}
                            >
                              <td className="px-4 py-3 text-gray-500 font-medium">{idx + 1}</td>
                              <td className="px-4 py-3 text-gray-600 font-mono text-xs">{student.admNo}</td>
                              <td className="px-4 py-3">
                                <div className="flex items-center gap-2.5">
                                  <div className="w-7 h-7 rounded-full bg-emerald-100 flex items-center justify-center flex-shrink-0">
                                    <span className="text-emerald-700 font-bold text-xs">{student.name.charAt(0)}</span>
                                  </div>
                                  <span className="font-medium text-gray-800">{student.name}</span>
                                </div>
                              </td>
                              <td className="px-4 py-3 text-gray-700">{student.class}</td>
                              <td className="px-4 py-3">
                                <span className="bg-gray-100 text-gray-600 text-xs font-semibold px-2 py-0.5 rounded-md">
                                  Sec {student.section}
                                </span>
                              </td>
                              <td className="px-4 py-3">
                                <PctBadge pct={student.percentage} />
                              </td>
                              <td className="px-4 py-3">
                                <div className="flex items-center gap-2">
                                  <textarea
                                    rows={1}
                                    value={smsMsg[student.id] || ""}
                                    onChange={(e) => setSmsMsg((prev) => ({ ...prev, [student.id]: e.target.value }))}
                                    placeholder="Type message..."
                                    className="text-xs border border-gray-200 rounded-lg px-2.5 py-1.5 resize-none w-44 focus:outline-none focus:ring-2 focus:ring-emerald-400"
                                  />
                                  <button
                                    onClick={() => handleSendSms(student, smsMsg[student.id] || "")}
                                    className="flex items-center gap-1 bg-emerald-600 hover:bg-emerald-700 active:scale-95 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition-all whitespace-nowrap"
                                  >
                                    <Send size={12} /> Send
                                  </button>
                                </div>
                              </td>
                            </tr>
                          ))
                        )}
                      </tbody>
                    </table>

                    {/* Footer summary */}
                    {filteredData.length > 0 && (
                      <div className="px-4 py-3 bg-gray-50 border-t border-gray-100 flex items-center justify-between text-xs text-gray-500">
                        <span>Showing <strong className="text-gray-700">{filteredData.length}</strong> records</span>
                        <div className="flex gap-4">
                          <span>Avg: <strong className="text-gray-700">{stats?.avg}%</strong></span>
                          <span className="text-emerald-600 font-medium">≥90%: {stats?.above90}</span>
                          <span className="text-red-500 font-medium">&lt;75%: {stats?.below75}</span>
                        </div>
                      </div>
                    )}
                  </div>
                </>
              )}
            </div>
          </main>
        </div>
      </div>

      {/* ── Mobile Filters Drawer ── */}
      {showFiltersDrawer && (
        <div className="fixed inset-0 z-50 lg:hidden">
          <div
            className="absolute inset-0 bg-black/40 backdrop-blur-sm"
            onClick={() => setShowFiltersDrawer(false)}
          />
          <div className="absolute bottom-0 left-0 right-0 bg-white rounded-t-3xl p-5 max-h-[90vh] overflow-y-auto shadow-2xl">
            <div className="flex items-center justify-between mb-5">
              <div className="flex items-center gap-2">
                <Filter size={16} className="text-emerald-600" />
                <h2 className="text-base font-bold text-gray-800">Report Filters</h2>
              </div>
              <button
                onClick={() => setShowFiltersDrawer(false)}
                className="w-8 h-8 flex items-center justify-center rounded-full bg-gray-100 hover:bg-gray-200 transition-colors"
              >
                <X size={16} className="text-gray-600" />
              </button>
            </div>
            <FilterPanel />
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast msg={toast} onClose={() => setToast(null)} />}
    </div>
  );
}
