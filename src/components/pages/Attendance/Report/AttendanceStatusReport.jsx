import { useState, useMemo, useCallback } from "react";
import {
  ChevronRight,
  Home,
  Download,
  RefreshCw,
  Search,
  Filter,
  X,
  ChevronDown,
  ChevronUp,
  Users,
  CheckCircle,
  XCircle,
  Calendar,
  BookOpen,
  BarChart3,
  FileSpreadsheet,
  Eye,
  AlertCircle,
  TrendingUp,
  Clock,
} from "lucide-react";

// ─── Static / Dummy Data ────────────────────────────────────────────────────

const SESSIONS = [
  { value: "0", label: "-- Select Session --" },
  { value: "2024-25", label: "2024-2025" },
  { value: "2023-24", label: "2023-2024" },
  { value: "2022-23", label: "2022-2023" },
];

const CLASSES = [
  { value: "0", label: "-- Select Class --" },
  { value: "1", label: "Class 1" },
  { value: "2", label: "Class 2" },
  { value: "3", label: "Class 3" },
  { value: "4", label: "Class 4" },
  { value: "5", label: "Class 5" },
  { value: "6", label: "Class 6" },
  { value: "7", label: "Class 7" },
  { value: "8", label: "Class 8" },
  { value: "9", label: "Class 9" },
  { value: "10", label: "Class 10" },
];

// Dummy attendance status data (attendance_status.aspx)
const DUMMY_ATTENDANCE_STATUS = [
  { rollNo: "101", name: "Aarav Sharma", class: "Class 5", section: "A", date: "06/04/2025", status: "Present", remarks: "" },
  { rollNo: "102", name: "Priya Singh", class: "Class 5", section: "A", date: "06/04/2025", status: "Absent", remarks: "Sick Leave" },
  { rollNo: "103", name: "Rohan Verma", class: "Class 5", section: "A", date: "06/04/2025", status: "Present", remarks: "" },
  { rollNo: "104", name: "Sneha Patel", class: "Class 5", section: "A", date: "06/04/2025", status: "Late", remarks: "Late by 20 min" },
  { rollNo: "105", name: "Karan Mehta", class: "Class 5", section: "A", date: "06/04/2025", status: "Present", remarks: "" },
  { rollNo: "106", name: "Anjali Gupta", class: "Class 5", section: "A", date: "06/04/2025", status: "Absent", remarks: "Family Function" },
  { rollNo: "107", name: "Vikas Kumar", class: "Class 5", section: "A", date: "06/04/2025", status: "Present", remarks: "" },
  { rollNo: "108", name: "Pooja Yadav", class: "Class 5", section: "A", date: "06/04/2025", status: "Present", remarks: "" },
  { rollNo: "109", name: "Arjun Nair", class: "Class 5", section: "A", date: "06/04/2025", status: "Absent", remarks: "Medical" },
  { rollNo: "110", name: "Riya Mishra", class: "Class 5", section: "A", date: "06/04/2025", status: "Present", remarks: "" },
  { rollNo: "111", name: "Deepak Joshi", class: "Class 5", section: "B", date: "06/04/2025", status: "Present", remarks: "" },
  { rollNo: "112", name: "Kavya Reddy", class: "Class 5", section: "B", date: "06/04/2025", status: "Late", remarks: "Transport delay" },
  { rollNo: "113", name: "Mohit Chauhan", class: "Class 5", section: "B", date: "06/04/2025", status: "Present", remarks: "" },
  { rollNo: "114", name: "Nisha Rawat", class: "Class 5", section: "B", date: "06/04/2025", status: "Absent", remarks: "" },
  { rollNo: "115", name: "Saurabh Tripathi", class: "Class 5", section: "B", date: "06/04/2025", status: "Present", remarks: "" },
];

// ─── Helper Utilities ────────────────────────────────────────────────────────

const getStatusConfig = (status) => {
  switch (status) {
    case "Present":
      return {
        color: "text-emerald-700",
        bg: "bg-emerald-50",
        border: "border-emerald-200",
        dot: "bg-emerald-500",
        icon: CheckCircle,
      };
    case "Absent":
      return {
        color: "text-red-700",
        bg: "bg-red-50",
        border: "border-red-200",
        dot: "bg-red-500",
        icon: XCircle,
      };
    case "Late":
      return {
        color: "text-amber-700",
        bg: "bg-amber-50",
        border: "border-amber-200",
        dot: "bg-amber-500",
        icon: Clock,
      };
    default:
      return {
        color: "text-slate-600",
        bg: "bg-slate-50",
        border: "border-slate-200",
        dot: "bg-slate-400",
        icon: AlertCircle,
      };
  }
};

// ─── Sub-Components ──────────────────────────────────────────────────────────

/** Breadcrumb bar */
const Breadcrumb = ({ items }) => (
  <nav className="flex items-center gap-1.5 text-sm text-slate-500 mb-6">
    <Home size={14} className="text-slate-400" />
    {items.map((item, i) => (
      <span key={i} className="flex items-center gap-1.5">
        <ChevronRight size={13} className="text-slate-300" />
        <span
          className={
            i === items.length - 1
              ? "text-slate-800 font-semibold"
              : "hover:text-slate-700 cursor-pointer transition-colors"
          }
        >
          {item}
        </span>
      </span>
    ))}
  </nav>
);

/** Summary stat card */
const StatCard = ({ icon: Icon, label, value, color, bg }) => (
  <div
    className={`flex items-center gap-3 rounded-xl p-4 border ${bg} border-opacity-60 shadow-sm`}
  >
    <div className={`p-2 rounded-lg ${color} bg-white shadow-sm`}>
      <Icon size={18} />
    </div>
    <div>
      <p className="text-xs text-slate-500 font-medium leading-none mb-0.5">{label}</p>
      <p className="text-xl font-bold text-slate-800 leading-none">{value}</p>
    </div>
  </div>
);

/** Status badge */
const StatusBadge = ({ status }) => {
  const cfg = getStatusConfig(status);
  const Icon = cfg.icon;
  return (
    <span
      className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold border ${cfg.bg} ${cfg.color} ${cfg.border}`}
    >
      <Icon size={11} />
      {status}
    </span>
  );
};

/** Desktop table row */
const TableRow = ({ row, idx }) => (
  <tr className="hover:bg-slate-50/80 transition-colors border-b border-slate-100 last:border-0">
    <td className="px-4 py-3 text-sm text-slate-500 font-medium">{idx + 1}</td>
    <td className="px-4 py-3 text-sm font-semibold text-slate-700">{row.rollNo}</td>
    <td className="px-4 py-3 text-sm text-slate-800 font-medium">{row.name}</td>
    <td className="px-4 py-3 text-sm text-slate-600">{row.class}</td>
    <td className="px-4 py-3 text-sm text-slate-600">{row.section}</td>
    <td className="px-4 py-3 text-sm text-slate-600">{row.date}</td>
    <td className="px-4 py-3">
      <StatusBadge status={row.status} />
    </td>
    <td className="px-4 py-3 text-sm text-slate-500 italic">
      {row.remarks || <span className="text-slate-300">—</span>}
    </td>
  </tr>
);

/** Mobile card */
const MobileCard = ({ row, idx }) => {
  const [expanded, setExpanded] = useState(false);
  const cfg = getStatusConfig(row.status);

  return (
    <div
      className={`rounded-xl border ${cfg.border} bg-white shadow-sm overflow-hidden transition-all`}
    >
      {/* Card header */}
      <div
        className="flex items-center gap-3 px-4 py-3 cursor-pointer"
        onClick={() => setExpanded((p) => !p)}
      >
        {/* Status dot */}
        <span className={`w-2.5 h-2.5 rounded-full flex-shrink-0 ${cfg.dot}`} />
        <div className="flex-1 min-w-0">
          <p className="text-sm font-bold text-slate-800 truncate">{row.name}</p>
          <p className="text-xs text-slate-500">
            Roll: {row.rollNo} &middot; {row.class} &middot; Sec {row.section}
          </p>
        </div>
        <StatusBadge status={row.status} />
        <button className="ml-1 text-slate-400">
          {expanded ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
        </button>
      </div>

      {/* Expandable detail */}
      {expanded && (
        <div className="px-4 pb-4 pt-1 border-t border-slate-100 bg-slate-50/50 grid grid-cols-2 gap-3">
          <div>
            <p className="text-xs text-slate-400 font-medium">Date</p>
            <p className="text-sm text-slate-700 font-semibold">{row.date}</p>
          </div>
          <div>
            <p className="text-xs text-slate-400 font-medium">Remarks</p>
            <p className="text-sm text-slate-700 font-semibold">
              {row.remarks || "—"}
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

/** Empty state */
const EmptyState = ({ filtered }) => (
  <div className="flex flex-col items-center justify-center py-16 text-center">
    <div className="w-16 h-16 rounded-2xl bg-slate-100 flex items-center justify-center mb-4">
      <Users size={28} className="text-slate-400" />
    </div>
    <p className="text-slate-700 font-semibold text-base mb-1">
      {filtered ? "No matching records" : "No data to display"}
    </p>
    <p className="text-slate-400 text-sm">
      {filtered
        ? "Try adjusting your search or filters."
        : "Select a session, class, and date then click Show."}
    </p>
  </div>
);

// ─── Main Page Component ─────────────────────────────────────────────────────

export default function AttendanceStatusReport() {
  // Filter state
  const [session, setSession] = useState("0");
  const [cls, setCls] = useState("0");
  const [date, setDate] = useState("");
  const [loading, setLoading] = useState(false);
  const [showResults, setShowResults] = useState(false);
  const [data, setData] = useState([]);

  // Search & filter on results
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("All");
  const [mobileFilterOpen, setMobileFilterOpen] = useState(false);

  // Validation error
  const [errors, setErrors] = useState({});

  /* ── Handlers ── */

  const validate = () => {
    const e = {};
    if (session === "0") e.session = "Please select a session.";
    if (cls === "0") e.cls = "Please select a class.";
    return e;
  };

  const handleShow = useCallback(() => {
    const e = validate();
    if (Object.keys(e).length) {
      setErrors(e);
      return;
    }
    setErrors({});
    setLoading(true);
    setShowResults(false);

    // Simulate API call
    setTimeout(() => {
      setData(DUMMY_ATTENDANCE_STATUS);
      setShowResults(true);
      setLoading(false);
    }, 900);
  }, [session, cls, date]);

  const handleReset = () => {
    setSession("0");
    setCls("0");
    setDate("");
    setSearch("");
    setStatusFilter("All");
    setShowResults(false);
    setData([]);
    setErrors({});
  };

  const handleExcelExport = () => {
    alert("Excel export triggered. Wire to backend API.");
  };

  /* ── Derived data ── */

  const filtered = useMemo(() => {
    let d = data;
    if (statusFilter !== "All") d = d.filter((r) => r.status === statusFilter);
    if (search.trim()) {
      const q = search.toLowerCase();
      d = d.filter(
        (r) =>
          r.name.toLowerCase().includes(q) ||
          r.rollNo.toLowerCase().includes(q) ||
          r.section.toLowerCase().includes(q)
      );
    }
    return d;
  }, [data, search, statusFilter]);

  const stats = useMemo(() => {
    const total = data.length;
    const present = data.filter((r) => r.status === "Present").length;
    const absent = data.filter((r) => r.status === "Absent").length;
    const late = data.filter((r) => r.status === "Late").length;
    const pct = total ? Math.round((present / total) * 100) : 0;
    return { total, present, absent, late, pct };
  }, [data]);

  /* ── Render ── */

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-slate-100">
      {/* Top accent bar */}
      <div className="h-1 w-full bg-gradient-to-r from-emerald-500 via-teal-500 to-cyan-500" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        {/* Breadcrumb */}
        <Breadcrumb items={["Home", "Reports", "Attendance Status Report"]} />

        {/* Page Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-6">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-xl bg-gradient-to-br from-emerald-500 to-teal-600 shadow-md shadow-emerald-200">
              <BarChart3 size={22} className="text-white" />
            </div>
            <div>
              <h1 className="text-xl font-extrabold text-slate-900 tracking-tight leading-none">
                Attendance Status Report
              </h1>
              <p className="text-xs text-slate-500 mt-0.5 font-medium">
                View student-wise daily attendance status
              </p>
            </div>
          </div>

          {showResults && (
            <div className="flex items-center gap-2">
              <button
                onClick={handleExcelExport}
                className="flex items-center gap-2 px-3 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold shadow-sm transition-all active:scale-95"
              >
                <FileSpreadsheet size={14} />
                <span>Export Excel</span>
              </button>
              <button
                onClick={handleReset}
                className="flex items-center gap-2 px-3 py-2 rounded-lg bg-white border border-slate-200 hover:bg-slate-50 text-slate-600 text-xs font-semibold shadow-sm transition-all active:scale-95"
              >
                <RefreshCw size={14} />
                <span>Reset</span>
              </button>
            </div>
          )}
        </div>

        {/* ── Filter Card ── */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm shadow-slate-100 p-5 mb-5">
          <div className="flex items-center gap-2 mb-4">
            <Filter size={15} className="text-slate-400" />
            <span className="text-sm font-bold text-slate-700">Filter Options</span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* Session */}
            <div>
              <label className="block text-xs font-bold text-slate-600 mb-1.5 uppercase tracking-wide">
                Session
              </label>
              <div className="relative">
                <select
                  value={session}
                  onChange={(e) => {
                    setSession(e.target.value);
                    setErrors((prev) => ({ ...prev, session: undefined }));
                  }}
                  className={`w-full appearance-none bg-slate-50 border ${
                    errors.session ? "border-red-400" : "border-slate-200"
                  } rounded-lg px-3 py-2.5 text-sm text-slate-700 font-medium focus:outline-none focus:ring-2 focus:ring-emerald-400 focus:border-transparent transition-all pr-8`}
                >
                  {SESSIONS.map((s) => (
                    <option key={s.value} value={s.value}>
                      {s.label}
                    </option>
                  ))}
                </select>
                <ChevronDown
                  size={14}
                  className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none"
                />
              </div>
              {errors.session && (
                <p className="mt-1 text-xs text-red-500 flex items-center gap-1">
                  <AlertCircle size={11} /> {errors.session}
                </p>
              )}
            </div>

            {/* Class */}
            <div>
              <label className="block text-xs font-bold text-slate-600 mb-1.5 uppercase tracking-wide">
                Class
              </label>
              <div className="relative">
                <select
                  value={cls}
                  onChange={(e) => {
                    setCls(e.target.value);
                    setErrors((prev) => ({ ...prev, cls: undefined }));
                  }}
                  className={`w-full appearance-none bg-slate-50 border ${
                    errors.cls ? "border-red-400" : "border-slate-200"
                  } rounded-lg px-3 py-2.5 text-sm text-slate-700 font-medium focus:outline-none focus:ring-2 focus:ring-emerald-400 focus:border-transparent transition-all pr-8`}
                >
                  {CLASSES.map((c) => (
                    <option key={c.value} value={c.value}>
                      {c.label}
                    </option>
                  ))}
                </select>
                <ChevronDown
                  size={14}
                  className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none"
                />
              </div>
              {errors.cls && (
                <p className="mt-1 text-xs text-red-500 flex items-center gap-1">
                  <AlertCircle size={11} /> {errors.cls}
                </p>
              )}
            </div>

            {/* Date */}
            <div>
              <label className="block text-xs font-bold text-slate-600 mb-1.5 uppercase tracking-wide">
                Attendance Date
              </label>
              <div className="relative">
                <input
                  type="date"
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                  className="w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2.5 text-sm text-slate-700 font-medium focus:outline-none focus:ring-2 focus:ring-emerald-400 focus:border-transparent transition-all"
                />
              </div>
            </div>

            {/* Action buttons */}
            <div className="flex items-end gap-2">
              <button
                onClick={handleShow}
                disabled={loading}
                className="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 rounded-lg bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700 text-white text-sm font-bold shadow-md shadow-emerald-200 transition-all active:scale-95 disabled:opacity-70 disabled:cursor-not-allowed"
              >
                {loading ? (
                  <RefreshCw size={15} className="animate-spin" />
                ) : (
                  <Eye size={15} />
                )}
                {loading ? "Loading..." : "Show"}
              </button>
              <button
                onClick={handleReset}
                className="p-2.5 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-500 transition-all active:scale-95"
                title="Reset Filters"
              >
                <X size={16} />
              </button>
            </div>
          </div>
        </div>

        {/* ── Results Section ── */}
        {showResults && (
          <>
            {/* Stats Row */}
            <div className="grid grid-cols-2 lg:grid-cols-4 gap-3 mb-5">
              <StatCard
                icon={Users}
                label="Total Students"
                value={stats.total}
                color="text-slate-600"
                bg="bg-slate-50 border-slate-200"
              />
              <StatCard
                icon={CheckCircle}
                label="Present"
                value={stats.present}
                color="text-emerald-600"
                bg="bg-emerald-50 border-emerald-200"
              />
              <StatCard
                icon={XCircle}
                label="Absent"
                value={stats.absent}
                color="text-red-500"
                bg="bg-red-50 border-red-200"
              />
              <StatCard
                icon={TrendingUp}
                label="Attendance %"
                value={`${stats.pct}%`}
                color="text-teal-600"
                bg="bg-teal-50 border-teal-200"
              />
            </div>

            {/* Data Card */}
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm shadow-slate-100 overflow-hidden">
              {/* Card Header: search + status filter */}
              <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-4 border-b border-slate-100">
                <div className="flex items-center gap-2 flex-1 min-w-0">
                  <BookOpen size={15} className="text-slate-400 flex-shrink-0" />
                  <span className="text-sm font-bold text-slate-700">
                    Attendance Records
                  </span>
                  <span className="ml-1 px-2 py-0.5 rounded-full bg-slate-100 text-slate-500 text-xs font-semibold">
                    {filtered.length}
                  </span>
                </div>

                <div className="flex items-center gap-2 w-full sm:w-auto">
                  {/* Search */}
                  <div className="relative flex-1 sm:w-52">
                    <Search
                      size={13}
                      className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
                    />
                    <input
                      type="text"
                      placeholder="Search name / roll..."
                      value={search}
                      onChange={(e) => setSearch(e.target.value)}
                      className="w-full pl-8 pr-3 py-2 text-xs font-medium bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-400 focus:border-transparent transition-all"
                    />
                    {search && (
                      <button
                        onClick={() => setSearch("")}
                        className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                      >
                        <X size={12} />
                      </button>
                    )}
                  </div>

                  {/* Status filter pills */}
                  <div className="hidden sm:flex items-center gap-1">
                    {["All", "Present", "Absent", "Late"].map((s) => (
                      <button
                        key={s}
                        onClick={() => setStatusFilter(s)}
                        className={`px-2.5 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                          statusFilter === s
                            ? "bg-emerald-600 text-white shadow-sm"
                            : "bg-slate-100 text-slate-500 hover:bg-slate-200"
                        }`}
                      >
                        {s}
                      </button>
                    ))}
                  </div>

                  {/* Mobile filter button */}
                  <button
                    onClick={() => setMobileFilterOpen((p) => !p)}
                    className="sm:hidden flex items-center gap-1.5 px-3 py-2 rounded-lg bg-slate-100 text-slate-600 text-xs font-semibold"
                  >
                    <Filter size={13} />
                    {statusFilter !== "All" && (
                      <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />
                    )}
                  </button>
                </div>
              </div>

              {/* Mobile status filter drawer */}
              {mobileFilterOpen && (
                <div className="sm:hidden px-5 py-3 border-b border-slate-100 bg-slate-50 flex flex-wrap gap-2">
                  {["All", "Present", "Absent", "Late"].map((s) => (
                    <button
                      key={s}
                      onClick={() => {
                        setStatusFilter(s);
                        setMobileFilterOpen(false);
                      }}
                      className={`px-3 py-1.5 rounded-full text-xs font-semibold transition-all ${
                        statusFilter === s
                          ? "bg-emerald-600 text-white"
                          : "bg-white border border-slate-200 text-slate-600"
                      }`}
                    >
                      {s}
                    </button>
                  ))}
                </div>
              )}

              {/* Desktop Table */}
              {filtered.length > 0 ? (
                <>
                  <div className="hidden md:block overflow-x-auto">
                    <table className="w-full text-left">
                      <thead>
                        <tr className="bg-slate-50 border-b border-slate-200">
                          {[
                            "S.No",
                            "Roll No",
                            "Student Name",
                            "Class",
                            "Section",
                            "Date",
                            "Status",
                            "Remarks",
                          ].map((h) => (
                            <th
                              key={h}
                              className="px-4 py-3 text-xs font-extrabold text-slate-500 uppercase tracking-wider whitespace-nowrap"
                            >
                              {h}
                            </th>
                          ))}
                        </tr>
                      </thead>
                      <tbody>
                        {filtered.map((row, idx) => (
                          <TableRow key={row.rollNo} row={row} idx={idx} />
                        ))}
                      </tbody>
                    </table>
                  </div>

                  {/* Mobile Cards */}
                  <div className="md:hidden p-4 flex flex-col gap-3">
                    {filtered.map((row, idx) => (
                      <MobileCard key={row.rollNo} row={row} idx={idx} />
                    ))}
                  </div>
                </>
              ) : (
                <EmptyState filtered={search || statusFilter !== "All"} />
              )}

              {/* Footer */}
              {filtered.length > 0 && (
                <div className="px-5 py-3 border-t border-slate-100 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
                  <p className="text-xs text-slate-400 font-medium">
                    Showing{" "}
                    <span className="text-slate-600 font-bold">{filtered.length}</span>{" "}
                    of{" "}
                    <span className="text-slate-600 font-bold">{data.length}</span>{" "}
                    records
                  </p>
                  <div className="flex items-center gap-3">
                    {/* Attendance % mini bar */}
                    <div className="flex items-center gap-2">
                      <span className="text-xs text-slate-500 font-medium hidden sm:inline">
                        Attendance
                      </span>
                      <div className="w-28 h-2 rounded-full bg-slate-100 overflow-hidden">
                        <div
                          className="h-full rounded-full bg-gradient-to-r from-emerald-400 to-teal-500 transition-all duration-700"
                          style={{ width: `${stats.pct}%` }}
                        />
                      </div>
                      <span className="text-xs font-bold text-emerald-600">
                        {stats.pct}%
                      </span>
                    </div>
                    <button
                      onClick={handleExcelExport}
                      className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-emerald-50 hover:bg-emerald-100 text-emerald-700 text-xs font-semibold border border-emerald-200 transition-all"
                    >
                      <Download size={12} />
                      Export
                    </button>
                  </div>
                </div>
              )}
            </div>
          </>
        )}

        {/* Initial empty prompt */}
        {!showResults && !loading && (
          <div className="bg-white rounded-2xl border border-dashed border-slate-200 p-10 flex flex-col items-center text-center">
            <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-emerald-50 to-teal-100 flex items-center justify-center mb-4 shadow-inner">
              <Calendar size={24} className="text-emerald-500" />
            </div>
            <p className="text-slate-700 font-bold text-base mb-1">
              Select filters to view attendance
            </p>
            <p className="text-slate-400 text-sm max-w-xs">
              Choose a session and class above, optionally pick a date, then click{" "}
              <strong className="text-emerald-600">Show</strong> to load the report.
            </p>
          </div>
        )}

        {/* Loading state */}
        {loading && (
          <div className="bg-white rounded-2xl border border-slate-200 p-12 flex flex-col items-center">
            <div className="w-10 h-10 rounded-full border-4 border-emerald-200 border-t-emerald-500 animate-spin mb-4" />
            <p className="text-slate-500 text-sm font-medium">
              Fetching attendance data…
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
