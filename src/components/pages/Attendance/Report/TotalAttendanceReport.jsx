import { useState, useMemo, useCallback } from "react";
import {
  BarChart3,
  Users,
  UserCheck,
  UserX,
  ChevronDown,
  Search,
  RefreshCw,
  Filter,
  TrendingUp,
  BookOpen,
  GraduationCap,
  Calendar,
  Eye,
  X,
  AlertCircle,
  CheckCircle2,
} from "lucide-react";

// ─────────────────────────────────────────────
// STATIC / DUMMY DATA
// ─────────────────────────────────────────────
const SESSIONS = [
  { id: "0", label: "-- Select Session --" },
  { id: "2023-24", label: "2023-24" },
  { id: "2024-25", label: "2024-25" },
  { id: "2025-26", label: "2025-26" },
];

const CLASSES = {
  "0": [{ id: "0", label: "-- Select Class --" }],
  "2023-24": [
    { id: "0", label: "-- Select Class --" },
    { id: "all", label: "All Classes" },
    { id: "1", label: "Class I" },
    { id: "2", label: "Class II" },
    { id: "3", label: "Class III" },
    { id: "4", label: "Class IV" },
    { id: "5", label: "Class V" },
  ],
  "2024-25": [
    { id: "0", label: "-- Select Class --" },
    { id: "all", label: "All Classes" },
    { id: "1", label: "Class I" },
    { id: "2", label: "Class II" },
    { id: "3", label: "Class III" },
    { id: "4", label: "Class IV" },
    { id: "5", label: "Class V" },
    { id: "6", label: "Class VI" },
  ],
  "2025-26": [
    { id: "0", label: "-- Select Class --" },
    { id: "all", label: "All Classes" },
    { id: "1", label: "Class I" },
    { id: "2", label: "Class II" },
    { id: "3", label: "Class III" },
    { id: "4", label: "Class IV" },
    { id: "5", label: "Class V" },
    { id: "6", label: "Class VI" },
    { id: "7", label: "Class VII" },
    { id: "8", label: "Class VIII" },
  ],
};

// Dummy attendance data per session+class
const ATTENDANCE_DATA = {
  "2025-26_all": [
    { class: "Class I",    total: 45, present: 40, absent: 5 },
    { class: "Class II",   total: 52, present: 48, absent: 4 },
    { class: "Class III",  total: 38, present: 33, absent: 5 },
    { class: "Class IV",   total: 60, present: 55, absent: 5 },
    { class: "Class V",    total: 47, present: 42, absent: 5 },
    { class: "Class VI",   total: 55, present: 50, absent: 5 },
    { class: "Class VII",  total: 41, present: 37, absent: 4 },
    { class: "Class VIII", total: 49, present: 44, absent: 5 },
  ],
  "2025-26_1": [{ class: "Class I",    total: 45, present: 40, absent: 5 }],
  "2025-26_2": [{ class: "Class II",   total: 52, present: 48, absent: 4 }],
  "2025-26_3": [{ class: "Class III",  total: 38, present: 33, absent: 5 }],
  "2025-26_4": [{ class: "Class IV",   total: 60, present: 55, absent: 5 }],
  "2025-26_5": [{ class: "Class V",    total: 47, present: 42, absent: 5 }],
  "2025-26_6": [{ class: "Class VI",   total: 55, present: 50, absent: 5 }],
  "2025-26_7": [{ class: "Class VII",  total: 41, present: 37, absent: 4 }],
  "2025-26_8": [{ class: "Class VIII", total: 49, present: 44, absent: 5 }],

  "2024-25_all": [
    { class: "Class I",   total: 43, present: 38, absent: 5 },
    { class: "Class II",  total: 50, present: 46, absent: 4 },
    { class: "Class III", total: 36, present: 30, absent: 6 },
    { class: "Class IV",  total: 58, present: 52, absent: 6 },
    { class: "Class V",   total: 44, present: 40, absent: 4 },
    { class: "Class VI",  total: 53, present: 47, absent: 6 },
  ],
  "2024-25_1": [{ class: "Class I",   total: 43, present: 38, absent: 5 }],
  "2024-25_2": [{ class: "Class II",  total: 50, present: 46, absent: 4 }],
  "2024-25_3": [{ class: "Class III", total: 36, present: 30, absent: 6 }],
  "2024-25_4": [{ class: "Class IV",  total: 58, present: 52, absent: 6 }],
  "2024-25_5": [{ class: "Class V",   total: 44, present: 40, absent: 4 }],
  "2024-25_6": [{ class: "Class VI",  total: 53, present: 47, absent: 6 }],

  "2023-24_all": [
    { class: "Class I",  total: 40, present: 35, absent: 5 },
    { class: "Class II", total: 48, present: 44, absent: 4 },
    { class: "Class III",total: 33, present: 28, absent: 5 },
    { class: "Class IV", total: 55, present: 50, absent: 5 },
    { class: "Class V",  total: 42, present: 37, absent: 5 },
  ],
  "2023-24_1": [{ class: "Class I",   total: 40, present: 35, absent: 5 }],
  "2023-24_2": [{ class: "Class II",  total: 48, present: 44, absent: 4 }],
  "2023-24_3": [{ class: "Class III", total: 33, present: 28, absent: 5 }],
  "2023-24_4": [{ class: "Class IV",  total: 55, present: 50, absent: 5 }],
  "2023-24_5": [{ class: "Class V",   total: 42, present: 37, absent: 5 }],
};

// ─────────────────────────────────────────────
// SMALL REUSABLE COMPONENTS
// ─────────────────────────────────────────────

/** Styled select dropdown */
function SelectField({ label, value, onChange, options, error }) {
  return (
    <div className="flex flex-col gap-1.5">
      <label className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
        {label}
      </label>
      <div className="relative">
        <select
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className={`w-full appearance-none bg-white border rounded-xl px-4 py-2.5 pr-10 text-sm font-medium text-slate-700 shadow-sm transition-all focus:outline-none focus:ring-2 focus:ring-emerald-400 focus:border-emerald-400 cursor-pointer
            ${error ? "border-red-400 ring-1 ring-red-300" : "border-slate-200 hover:border-slate-300"}`}
        >
          {options.map((o) => (
            <option key={o.id} value={o.id}>
              {o.label}
            </option>
          ))}
        </select>
        <ChevronDown
          size={16}
          className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none"
        />
      </div>
      {error && (
        <span className="text-xs text-red-500 flex items-center gap-1">
          <AlertCircle size={12} /> {error}
        </span>
      )}
    </div>
  );
}

/** Summary stat card */
function StatCard({ icon: Icon, label, value, color }) {
  const colors = {
    blue:    "from-blue-50 to-blue-100 text-blue-700 border-blue-200",
    emerald: "from-emerald-50 to-emerald-100 text-emerald-700 border-emerald-200",
    rose:    "from-rose-50 to-rose-100 text-rose-700 border-rose-200",
    violet:  "from-violet-50 to-violet-100 text-violet-700 border-violet-200",
  };
  const iconColors = {
    blue:    "bg-blue-500",
    emerald: "bg-emerald-500",
    rose:    "bg-rose-500",
    violet:  "bg-violet-500",
  };
  return (
    <div className={`bg-gradient-to-br ${colors[color]} border rounded-2xl p-4 flex items-center gap-4 shadow-sm`}>
      <div className={`${iconColors[color]} p-2.5 rounded-xl shadow-sm`}>
        <Icon size={20} className="text-white" />
      </div>
      <div>
        <p className="text-xs font-semibold opacity-70 uppercase tracking-wide">{label}</p>
        <p className="text-2xl font-bold">{value}</p>
      </div>
    </div>
  );
}

/** Progress bar for attendance percentage */
function AttendanceBar({ present, total }) {
  const pct = total > 0 ? Math.round((present / total) * 100) : 0;
  const color =
    pct >= 90 ? "bg-emerald-500" :
    pct >= 75 ? "bg-amber-400" :
    "bg-rose-500";
  return (
    <div className="flex items-center gap-2 min-w-[90px]">
      <div className="flex-1 bg-slate-100 rounded-full h-2 overflow-hidden">
        <div
          className={`h-2 rounded-full transition-all duration-700 ${color}`}
          style={{ width: `${pct}%` }}
        />
      </div>
      <span className={`text-xs font-bold w-9 text-right ${
        pct >= 90 ? "text-emerald-600" : pct >= 75 ? "text-amber-600" : "text-rose-600"
      }`}>{pct}%</span>
    </div>
  );
}

/** Mobile card for one class row */
function MobileAttendanceCard({ row, index }) {
  const pct = row.total > 0 ? Math.round((row.present / row.total) * 100) : 0;
  const [open, setOpen] = useState(false);
  return (
    <div className="bg-white border border-slate-100 rounded-2xl shadow-sm overflow-hidden">
      {/* Card header */}
      <button
        onClick={() => setOpen(!open)}
        className="w-full flex items-center justify-between p-4 hover:bg-slate-50 transition-colors"
      >
        <div className="flex items-center gap-3">
          <div className="bg-gradient-to-br from-emerald-400 to-teal-500 text-white w-9 h-9 rounded-xl flex items-center justify-center text-sm font-bold shadow-sm">
            {index}
          </div>
          <div className="text-left">
            <p className="font-semibold text-slate-800 text-sm">{row.class}</p>
            <p className="text-xs text-slate-400">{row.total} Students</p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <span className={`text-sm font-bold ${
            pct >= 90 ? "text-emerald-600" : pct >= 75 ? "text-amber-600" : "text-rose-600"
          }`}>{pct}%</span>
          <ChevronDown
            size={16}
            className={`text-slate-400 transition-transform duration-300 ${open ? "rotate-180" : ""}`}
          />
        </div>
      </button>

      {/* Expandable detail */}
      {open && (
        <div className="border-t border-slate-100 bg-slate-50 px-4 pb-4 pt-3 grid grid-cols-3 gap-3 animate-in slide-in-from-top-1 duration-200">
          <div className="text-center bg-white rounded-xl p-3 shadow-sm">
            <Users size={16} className="text-blue-500 mx-auto mb-1" />
            <p className="text-lg font-bold text-slate-800">{row.total}</p>
            <p className="text-xs text-slate-400">Total</p>
          </div>
          <div className="text-center bg-white rounded-xl p-3 shadow-sm">
            <UserCheck size={16} className="text-emerald-500 mx-auto mb-1" />
            <p className="text-lg font-bold text-emerald-700">{row.present}</p>
            <p className="text-xs text-slate-400">Present</p>
          </div>
          <div className="text-center bg-white rounded-xl p-3 shadow-sm">
            <UserX size={16} className="text-rose-500 mx-auto mb-1" />
            <p className="text-lg font-bold text-rose-600">{row.absent}</p>
            <p className="text-xs text-slate-400">Absent</p>
          </div>
          <div className="col-span-3 mt-1">
            <AttendanceBar present={row.present} total={row.total} />
          </div>
        </div>
      )}
    </div>
  );
}

// ─────────────────────────────────────────────
// MAIN PAGE COMPONENT
// ─────────────────────────────────────────────
export default function TotalAttendanceReport() {
  const [session, setSession] = useState("0");
  const [classId, setClassId] = useState("0");
  const [errors, setErrors] = useState({});
  const [reportData, setReportData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [reportMeta, setReportMeta] = useState(null);

  // Update class list when session changes
  const classOptions = useMemo(
    () => CLASSES[session] || CLASSES["0"],
    [session]
  );

  const handleSessionChange = useCallback((val) => {
    setSession(val);
    setClassId("0");
    setReportData(null);
    setErrors({});
  }, []);

  /** Validate & fetch report */
  const handleShow = useCallback(() => {
    const errs = {};
    if (session === "0") errs.session = "Please select a session";
    if (classId === "0") errs.classId = "Please select a class";
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;

    setLoading(true);
    setReportData(null);

    // Simulate async API call
    setTimeout(() => {
      const key = `${session}_${classId}`;
      const data = ATTENDANCE_DATA[key] || [];
      setReportData(data);
      const sessionLabel = SESSIONS.find((s) => s.id === session)?.label || "";
      const classLabel = classOptions.find((c) => c.id === classId)?.label || "";
      setReportMeta({ session: sessionLabel, class: classLabel });
      setLoading(false);
    }, 600);
  }, [session, classId, classOptions]);

  const handleReset = () => {
    setSession("0");
    setClassId("0");
    setReportData(null);
    setErrors({});
    setSearchQuery("");
    setReportMeta(null);
  };

  /** Filtered rows based on search */
  const filteredData = useMemo(() => {
    if (!reportData) return [];
    if (!searchQuery.trim()) return reportData;
    return reportData.filter((r) =>
      r.class.toLowerCase().includes(searchQuery.toLowerCase())
    );
  }, [reportData, searchQuery]);

  /** Summary totals */
  const totals = useMemo(() => {
    if (!filteredData.length) return { total: 0, present: 0, absent: 0 };
    return filteredData.reduce(
      (acc, r) => ({
        total: acc.total + r.total,
        present: acc.present + r.present,
        absent: acc.absent + r.absent,
      }),
      { total: 0, present: 0, absent: 0 }
    );
  }, [filteredData]);

  const overallPct =
    totals.total > 0 ? Math.round((totals.present / totals.total) * 100) : 0;

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-slate-100 to-emerald-50/30 font-sans">

      {/* ── TOP HEADER ── */}
      <header className="bg-white border-b border-slate-200 shadow-sm sticky top-0 z-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-3 flex items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="bg-gradient-to-br from-emerald-500 to-teal-600 p-2 rounded-xl shadow-sm">
              <BarChart3 size={20} className="text-white" />
            </div>
            <div>
              <h1 className="text-base sm:text-lg font-bold text-slate-800 leading-tight">
                Total Attendance Report
              </h1>
              <nav className="hidden sm:flex items-center gap-1 text-xs text-slate-400">
                <span>Home</span>
                <span>/</span>
                <span>Reports</span>
                <span>/</span>
                <span className="text-emerald-600 font-medium">Total Attendance</span>
              </nav>
            </div>
          </div>
          <button
            onClick={handleReset}
            className="flex items-center gap-1.5 text-xs text-slate-500 hover:text-emerald-600 border border-slate-200 hover:border-emerald-300 px-3 py-2 rounded-xl transition-all hover:bg-emerald-50"
          >
            <RefreshCw size={13} />
            <span className="hidden sm:inline">Reset</span>
          </button>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 py-6 space-y-6">

        {/* ── FILTER CARD ── */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-5">
          <div className="flex items-center gap-2 mb-4">
            <Filter size={15} className="text-emerald-500" />
            <h2 className="text-sm font-bold text-slate-700 uppercase tracking-wide">
              Select Filters
            </h2>
          </div>

          {/* Filter row — stacks on mobile */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 items-end">
            <SelectField
              label="Academic Session"
              value={session}
              onChange={handleSessionChange}
              options={SESSIONS}
              error={errors.session}
            />
            <SelectField
              label="Class"
              value={classId}
              onChange={(v) => { setClassId(v); setErrors((e) => ({ ...e, classId: undefined })); }}
              options={classOptions}
              error={errors.classId}
            />

            {/* Spacer on large screens */}
            <div className="hidden lg:block" />

            {/* Show button */}
            <button
              onClick={handleShow}
              disabled={loading}
              className="flex items-center justify-center gap-2 bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-600 hover:to-teal-600 disabled:opacity-60 text-white font-semibold text-sm px-6 py-2.5 rounded-xl shadow-sm transition-all active:scale-95"
            >
              {loading ? (
                <>
                  <RefreshCw size={15} className="animate-spin" />
                  Loading…
                </>
              ) : (
                <>
                  <Eye size={15} />
                  Show Report
                </>
              )}
            </button>
          </div>
        </div>

        {/* ── LOADING SKELETON ── */}
        {loading && (
          <div className="space-y-3 animate-pulse">
            <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
              {[...Array(4)].map((_, i) => (
                <div key={i} className="h-20 bg-slate-100 rounded-2xl" />
              ))}
            </div>
            <div className="h-48 bg-slate-100 rounded-2xl" />
          </div>
        )}

        {/* ── REPORT RESULTS ── */}
        {!loading && reportData !== null && (
          <>
            {/* Meta badge */}
            <div className="flex flex-wrap items-center gap-2">
              <span className="text-xs bg-emerald-100 text-emerald-700 border border-emerald-200 px-3 py-1.5 rounded-full font-semibold flex items-center gap-1.5">
                <Calendar size={12} />
                {reportMeta?.session}
              </span>
              <span className="text-xs bg-violet-100 text-violet-700 border border-violet-200 px-3 py-1.5 rounded-full font-semibold flex items-center gap-1.5">
                <GraduationCap size={12} />
                {reportMeta?.class}
              </span>
              {filteredData.length === 0 ? (
                <span className="text-xs bg-rose-100 text-rose-600 border border-rose-200 px-3 py-1.5 rounded-full font-semibold flex items-center gap-1.5">
                  <AlertCircle size={12} /> No records found
                </span>
              ) : (
                <span className="text-xs bg-slate-100 text-slate-600 border border-slate-200 px-3 py-1.5 rounded-full font-semibold">
                  {filteredData.length} class{filteredData.length !== 1 ? "es" : ""}
                </span>
              )}
            </div>

            {/* ── SUMMARY STAT CARDS ── */}
            {filteredData.length > 0 && (
              <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
                <StatCard icon={Users}     label="Total Students" value={totals.total}   color="blue" />
                <StatCard icon={UserCheck} label="Present Today"  value={totals.present} color="emerald" />
                <StatCard icon={UserX}     label="Absent Today"   value={totals.absent}  color="rose" />
                <StatCard icon={TrendingUp} label="Attendance %"  value={`${overallPct}%`} color="violet" />
              </div>
            )}

            {/* ── SEARCH BAR ── */}
            {filteredData.length > 0 || searchQuery ? (
              <div className="flex items-center gap-3">
                <div className="relative flex-1 max-w-xs">
                  <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                  <input
                    type="text"
                    placeholder="Search class…"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="w-full pl-9 pr-4 py-2 text-sm border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-400 bg-white shadow-sm"
                  />
                  {searchQuery && (
                    <button
                      onClick={() => setSearchQuery("")}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                    >
                      <X size={13} />
                    </button>
                  )}
                </div>
              </div>
            ) : null}

            {/* ── EMPTY STATE ── */}
            {filteredData.length === 0 && (
              <div className="text-center py-16 bg-white rounded-2xl border border-slate-100 shadow-sm">
                <BookOpen size={40} className="text-slate-300 mx-auto mb-3" />
                <p className="text-slate-500 font-semibold">No Records Found</p>
                <p className="text-xs text-slate-400 mt-1">
                  {searchQuery ? "Try a different search." : "No attendance data available for the selected criteria."}
                </p>
              </div>
            )}

            {/* ── DESKTOP TABLE (hidden on mobile) ── */}
            {filteredData.length > 0 && (
              <div className="hidden md:block bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="bg-gradient-to-r from-slate-700 to-slate-800">
                        <th className="text-left px-5 py-3.5 text-xs font-semibold text-slate-200 uppercase tracking-wider w-12">
                          S.No.
                        </th>
                        <th className="text-left px-5 py-3.5 text-xs font-semibold text-slate-200 uppercase tracking-wider">
                          Class
                        </th>
                        <th className="text-center px-5 py-3.5 text-xs font-semibold text-slate-200 uppercase tracking-wider">
                          Total Students
                        </th>
                        <th className="text-center px-5 py-3.5 text-xs font-semibold text-emerald-300 uppercase tracking-wider">
                          Present
                        </th>
                        <th className="text-center px-5 py-3.5 text-xs font-semibold text-rose-300 uppercase tracking-wider">
                          Absent
                        </th>
                        <th className="text-left px-5 py-3.5 text-xs font-semibold text-slate-200 uppercase tracking-wider min-w-[160px]">
                          Attendance %
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      {filteredData.map((row, idx) => (
                        <tr
                          key={row.class}
                          className={`border-b border-slate-50 transition-colors hover:bg-emerald-50/30 ${
                            idx % 2 === 0 ? "bg-white" : "bg-slate-50/50"
                          }`}
                        >
                          <td className="px-5 py-4 text-slate-400 font-medium text-center">{idx + 1}</td>
                          <td className="px-5 py-4">
                            <div className="flex items-center gap-2.5">
                              <div className="bg-gradient-to-br from-emerald-400 to-teal-500 w-7 h-7 rounded-lg flex items-center justify-center shadow-sm">
                                <GraduationCap size={13} className="text-white" />
                              </div>
                              <span className="font-semibold text-slate-800">{row.class}</span>
                            </div>
                          </td>
                          <td className="px-5 py-4 text-center">
                            <span className="inline-flex items-center gap-1 bg-blue-50 text-blue-700 px-3 py-1 rounded-full text-sm font-semibold">
                              <Users size={12} />
                              {row.total}
                            </span>
                          </td>
                          <td className="px-5 py-4 text-center">
                            <span className="inline-flex items-center gap-1 bg-emerald-50 text-emerald-700 px-3 py-1 rounded-full text-sm font-semibold">
                              <CheckCircle2 size={12} />
                              {row.present}
                            </span>
                          </td>
                          <td className="px-5 py-4 text-center">
                            <span className="inline-flex items-center gap-1 bg-rose-50 text-rose-600 px-3 py-1 rounded-full text-sm font-semibold">
                              <UserX size={12} />
                              {row.absent}
                            </span>
                          </td>
                          <td className="px-5 py-4">
                            <AttendanceBar present={row.present} total={row.total} />
                          </td>
                        </tr>
                      ))}

                      {/* Totals row */}
                      <tr className="bg-gradient-to-r from-slate-100 to-slate-50 border-t-2 border-slate-200">
                        <td className="px-5 py-4" />
                        <td className="px-5 py-4 font-bold text-slate-800 text-sm uppercase tracking-wide">
                          Grand Total
                        </td>
                        <td className="px-5 py-4 text-center font-bold text-blue-700">{totals.total}</td>
                        <td className="px-5 py-4 text-center font-bold text-emerald-700">{totals.present}</td>
                        <td className="px-5 py-4 text-center font-bold text-rose-600">{totals.absent}</td>
                        <td className="px-5 py-4">
                          <AttendanceBar present={totals.present} total={totals.total} />
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* ── MOBILE CARDS (hidden on desktop) ── */}
            {filteredData.length > 0 && (
              <div className="md:hidden space-y-3">
                {filteredData.map((row, idx) => (
                  <MobileAttendanceCard key={row.class} row={row} index={idx + 1} />
                ))}

                {/* Mobile grand total card */}
                <div className="bg-gradient-to-br from-slate-700 to-slate-800 rounded-2xl p-4 shadow-md">
                  <p className="text-xs font-bold text-slate-300 uppercase tracking-wide mb-3">
                    Grand Total
                  </p>
                  <div className="grid grid-cols-3 gap-3">
                    <div className="text-center">
                      <p className="text-xl font-bold text-white">{totals.total}</p>
                      <p className="text-xs text-slate-400">Total</p>
                    </div>
                    <div className="text-center">
                      <p className="text-xl font-bold text-emerald-400">{totals.present}</p>
                      <p className="text-xs text-slate-400">Present</p>
                    </div>
                    <div className="text-center">
                      <p className="text-xl font-bold text-rose-400">{totals.absent}</p>
                      <p className="text-xs text-slate-400">Absent</p>
                    </div>
                  </div>
                  <div className="mt-3">
                    <AttendanceBar present={totals.present} total={totals.total} />
                  </div>
                </div>
              </div>
            )}
          </>
        )}

        {/* ── INITIAL EMPTY STATE (before any search) ── */}
        {!loading && reportData === null && (
          <div className="text-center py-20 bg-white rounded-2xl border border-dashed border-slate-200 shadow-sm">
            <div className="bg-emerald-50 w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <BarChart3 size={28} className="text-emerald-400" />
            </div>
            <p className="text-slate-600 font-semibold text-base">Select filters to generate report</p>
            <p className="text-xs text-slate-400 mt-1.5 max-w-xs mx-auto">
              Choose a session and class above, then click <strong>Show Report</strong> to view attendance data.
            </p>
          </div>
        )}
      </main>
    </div>
  );
}
