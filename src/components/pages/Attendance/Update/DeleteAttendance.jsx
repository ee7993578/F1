import { useState, useMemo, useCallback } from "react";
import {
  Trash2,
  BarChart3,
  Search,
  Calendar,
  ChevronDown,
  ChevronUp,
  AlertTriangle,
  CheckCircle2,
  XCircle,
  Users,
  BookOpen,
  Filter,
  RefreshCw,
  Eye,
  FileText,
  Download,
  Home,
  ChevronRight,
  X,
  SlidersHorizontal,
  UserCheck,
  UserX,
  Hash,
} from "lucide-react";

// ─────────────────────────────────────────────
// STATIC / DUMMY DATA
// ─────────────────────────────────────────────
const SESSIONS = [
  { id: "0", label: "Select Session" },
  { id: "2023-24", label: "2023-24" },
  { id: "2024-25", label: "2024-25" },
  { id: "2025-26", label: "2025-26" },
];

const CLASSES = [
  { id: "0", label: "Select Class" },
  { id: "1", label: "Class I" },
  { id: "2", label: "Class II" },
  { id: "3", label: "Class III" },
  { id: "4", label: "Class IV" },
  { id: "5", label: "Class V" },
  { id: "6", label: "Class VI" },
  { id: "7", label: "Class VII" },
  { id: "8", label: "Class VIII" },
  { id: "9", label: "Class IX" },
  { id: "10", label: "Class X" },
];

const ATTENDANCE_RECORDS = [
  { id: 1, admNo: "ADM001", name: "Aarav Sharma", class: "Class V", date: "05 Jun 2025", status: "Present" },
  { id: 2, admNo: "ADM002", name: "Priya Singh", class: "Class V", date: "05 Jun 2025", status: "Absent" },
  { id: 3, admNo: "ADM003", name: "Rohit Kumar", class: "Class V", date: "05 Jun 2025", status: "Present" },
  { id: 4, admNo: "ADM004", name: "Anjali Gupta", class: "Class V", date: "04 Jun 2025", status: "Present" },
  { id: 5, admNo: "ADM005", name: "Vikram Yadav", class: "Class V", date: "04 Jun 2025", status: "Absent" },
  { id: 6, admNo: "ADM006", name: "Meera Patel", class: "Class V", date: "03 Jun 2025", status: "Present" },
];

const REPORT_DATA = [
  { id: 1, class: "Class I", section: "A", total: 42, present: 38, absent: 4 },
  { id: 2, class: "Class II", section: "A", total: 45, present: 41, absent: 4 },
  { id: 3, class: "Class III", section: "B", total: 39, present: 35, absent: 4 },
  { id: 4, class: "Class IV", section: "A", total: 48, present: 44, absent: 4 },
  { id: 5, class: "Class V", section: "C", total: 40, present: 33, absent: 7 },
  { id: 6, class: "Class VI", section: "A", total: 52, present: 49, absent: 3 },
  { id: 7, class: "Class VII", section: "B", total: 47, present: 42, absent: 5 },
  { id: 8, class: "Class VIII", section: "A", total: 44, present: 40, absent: 4 },
];

// ─────────────────────────────────────────────
// REUSABLE COMPONENTS
// ─────────────────────────────────────────────

/** Breadcrumb */
const Breadcrumb = ({ items }) => (
  <nav className="flex items-center gap-1.5 text-sm mb-6">
    <Home className="w-3.5 h-3.5 text-emerald-500" />
    {items.map((item, i) => (
      <span key={i} className="flex items-center gap-1.5">
        <ChevronRight className="w-3.5 h-3.5 text-slate-400" />
        <span className={i === items.length - 1 ? "text-slate-700 font-semibold" : "text-emerald-600 cursor-pointer hover:underline"}>
          {item}
        </span>
      </span>
    ))}
  </nav>
);

/** Section Card wrapper */
const Card = ({ children, className = "" }) => (
  <div className={`bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden ${className}`}>
    {children}
  </div>
);

const CardHeader = ({ title, icon: Icon, subtitle }) => (
  <div className="flex items-center gap-3 px-6 py-4 border-b border-slate-100 bg-gradient-to-r from-emerald-50/60 to-white">
    {Icon && (
      <div className="w-9 h-9 rounded-xl bg-emerald-100 flex items-center justify-center">
        <Icon className="w-4.5 h-4.5 text-emerald-600" size={18} />
      </div>
    )}
    <div>
      <h2 className="text-base font-bold text-slate-800 leading-tight">{title}</h2>
      {subtitle && <p className="text-xs text-slate-500 mt-0.5">{subtitle}</p>}
    </div>
  </div>
);

/** Form Input */
const FormInput = ({ label, id, value, onChange, placeholder, readOnly, icon: Icon }) => (
  <div className="flex flex-col gap-1.5">
    <label htmlFor={id} className="text-xs font-semibold text-slate-600 uppercase tracking-wide">
      {label}
    </label>
    <div className="relative">
      {Icon && (
        <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">
          <Icon size={15} />
        </span>
      )}
      <input
        id={id}
        type="text"
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        readOnly={readOnly}
        className={`w-full rounded-xl border border-slate-200 bg-slate-50 text-sm text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-400 focus:border-transparent transition-all ${Icon ? "pl-9 pr-3 py-2.5" : "px-3 py-2.5"} ${readOnly ? "cursor-pointer" : ""}`}
      />
    </div>
  </div>
);

/** Select Dropdown */
const FormSelect = ({ label, id, value, onChange, options, error }) => (
  <div className="flex flex-col gap-1.5">
    <label htmlFor={id} className="text-xs font-semibold text-slate-600 uppercase tracking-wide">
      {label}
    </label>
    <div className="relative">
      <select
        id={id}
        value={value}
        onChange={onChange}
        className={`w-full appearance-none rounded-xl border bg-slate-50 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-emerald-400 focus:border-transparent transition-all px-3 py-2.5 pr-9 ${error ? "border-red-300 ring-1 ring-red-300" : "border-slate-200"}`}
      >
        {options.map((o) => (
          <option key={o.id} value={o.id}>{o.label}</option>
        ))}
      </select>
      <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
    </div>
    {error && <p className="text-xs text-red-500 flex items-center gap-1"><AlertTriangle size={11} />{error}</p>}
  </div>
);

/** Status Badge */
const StatusBadge = ({ status }) => {
  const map = {
    Present: "bg-emerald-50 text-emerald-700 border-emerald-200",
    Absent: "bg-red-50 text-red-600 border-red-200",
  };
  return (
    <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold border ${map[status] || "bg-slate-100 text-slate-600"}`}>
      {status === "Present" ? <UserCheck size={11} /> : <UserX size={11} />}
      {status}
    </span>
  );
};

/** Stat pill */
const StatPill = ({ label, value, color }) => (
  <div className={`flex flex-col items-center justify-center px-4 py-2 rounded-xl ${color}`}>
    <span className="text-lg font-bold leading-none">{value}</span>
    <span className="text-xs mt-0.5 font-medium opacity-80">{label}</span>
  </div>
);

/** Confirm Modal */
const ConfirmModal = ({ open, onConfirm, onCancel, count }) => {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm p-6 animate-in fade-in zoom-in-95">
        <div className="flex items-center gap-3 mb-4">
          <div className="w-11 h-11 rounded-2xl bg-red-100 flex items-center justify-center flex-shrink-0">
            <AlertTriangle className="text-red-500" size={22} />
          </div>
          <div>
            <h3 className="font-bold text-slate-800 text-base">Confirm Delete</h3>
            <p className="text-sm text-slate-500 mt-0.5">This action cannot be undone.</p>
          </div>
        </div>
        <p className="text-sm text-slate-600 bg-red-50 rounded-xl px-4 py-3 mb-5 border border-red-100">
          You are about to delete <strong>{count}</strong> attendance record{count !== 1 ? "s" : ""}. Are you sure?
        </p>
        <div className="flex gap-3">
          <button onClick={onCancel} className="flex-1 py-2.5 rounded-xl border border-slate-200 text-slate-600 text-sm font-semibold hover:bg-slate-50 transition-colors">
            Cancel
          </button>
          <button onClick={onConfirm} className="flex-1 py-2.5 rounded-xl bg-red-500 text-white text-sm font-semibold hover:bg-red-600 transition-colors flex items-center justify-center gap-2">
            <Trash2 size={14} /> Delete
          </button>
        </div>
      </div>
    </div>
  );
};

/** Toast */
const Toast = ({ msg, type, onClose }) => {
  if (!msg) return null;
  const cfg = {
    success: { bg: "bg-emerald-600", icon: CheckCircle2 },
    error: { bg: "bg-red-500", icon: XCircle },
  };
  const { bg, icon: Icon } = cfg[type] || cfg.success;
  return (
    <div className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 px-5 py-3 rounded-2xl text-white text-sm font-semibold shadow-xl ${bg} max-w-xs w-full`}>
      <Icon size={18} />
      <span className="flex-1">{msg}</span>
      <button onClick={onClose}><X size={16} /></button>
    </div>
  );
};

// ─────────────────────────────────────────────
// TAB: DELETE ATTENDANCE
// ─────────────────────────────────────────────
const DeleteAttendanceTab = () => {
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [admNo, setAdmNo] = useState("");
  const [records, setRecords] = useState([]);
  const [selected, setSelected] = useState([]);
  const [searched, setSearched] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [toast, setToast] = useState(null);
  const [loading, setLoading] = useState(false);

  const showToast = (msg, type = "success") => {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3000);
  };

  const handleSearch = useCallback(() => {
    if (!fromDate && !toDate && !admNo.trim()) {
      showToast("Please enter at least one search filter.", "error");
      return;
    }
    setLoading(true);
    setTimeout(() => {
      // Simulate filtering
      let filtered = [...ATTENDANCE_RECORDS];
      if (admNo.trim()) filtered = filtered.filter((r) => r.admNo.toLowerCase().includes(admNo.toLowerCase()) || r.name.toLowerCase().includes(admNo.toLowerCase()));
      setRecords(filtered);
      setSelected([]);
      setSearched(true);
      setLoading(false);
    }, 600);
  }, [fromDate, toDate, admNo]);

  const handleReset = () => {
    setFromDate(""); setToDate(""); setAdmNo("");
    setRecords([]); setSelected([]); setSearched(false);
  };

  const toggleSelect = (id) => setSelected((prev) => prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]);
  const toggleAll = () => setSelected(selected.length === records.length ? [] : records.map((r) => r.id));

  const handleDelete = () => {
    if (!selected.length) { showToast("Please select records to delete.", "error"); return; }
    setConfirmOpen(true);
  };

  const confirmDelete = () => {
    setRecords((prev) => prev.filter((r) => !selected.includes(r.id)));
    setSelected([]);
    setConfirmOpen(false);
    showToast(`${selected.length} record(s) deleted successfully.`);
  };

  return (
    <>
      <div className="space-y-5">
        {/* Filter Card */}
        <Card>
          <CardHeader title="Search Attendance Records" icon={Search} subtitle="Filter by date range or admission number" />
          <div className="p-5 space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              <FormInput label="From Date" id="fromDate" value={fromDate} onChange={(e) => setFromDate(e.target.value)} placeholder="DD MMM YYYY" icon={Calendar} />
              <FormInput label="To Date" id="toDate" value={toDate} onChange={(e) => setToDate(e.target.value)} placeholder="DD MMM YYYY" icon={Calendar} />
              <FormInput label="Admission No / Name" id="admNo" value={admNo} onChange={(e) => setAdmNo(e.target.value)} placeholder="e.g. ADM001 or student name" icon={Hash} />
            </div>
            <div className="flex flex-wrap gap-3 pt-1">
              <button onClick={handleSearch} disabled={loading} className="flex items-center gap-2 px-5 py-2.5 bg-emerald-500 hover:bg-emerald-600 text-white text-sm font-semibold rounded-xl transition-all shadow-sm disabled:opacity-60">
                {loading ? <RefreshCw size={15} className="animate-spin" /> : <Search size={15} />}
                {loading ? "Searching…" : "Search Records"}
              </button>
              <button onClick={handleReset} className="flex items-center gap-2 px-4 py-2.5 border border-slate-200 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-50 transition-colors">
                <RefreshCw size={14} /> Reset
              </button>
            </div>
          </div>
        </Card>

        {/* Results */}
        {searched && (
          <Card>
            <CardHeader
              title={`Records Found (${records.length})`}
              icon={FileText}
              subtitle={selected.length > 0 ? `${selected.length} selected` : "Select records to delete"}
            />
            <div className="p-5">
              {records.length === 0 ? (
                <div className="text-center py-12">
                  <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-3">
                    <FileText className="text-slate-400" size={28} />
                  </div>
                  <p className="text-slate-500 font-medium">No records found</p>
                  <p className="text-slate-400 text-sm mt-1">Try adjusting your search filters</p>
                </div>
              ) : (
                <>
                  {/* Desktop Table */}
                  <div className="hidden md:block rounded-xl overflow-hidden border border-slate-100">
                    <table className="w-full text-sm">
                      <thead>
                        <tr className="bg-slate-50 border-b border-slate-100">
                          <th className="px-4 py-3 text-left">
                            <input type="checkbox" checked={selected.length === records.length} onChange={toggleAll} className="w-4 h-4 accent-emerald-500 cursor-pointer" />
                          </th>
                          {["S.No.", "Adm No.", "Student Name", "Class", "Date", "Status"].map((h) => (
                            <th key={h} className="px-4 py-3 text-left text-xs font-bold text-slate-500 uppercase tracking-wide">{h}</th>
                          ))}
                        </tr>
                      </thead>
                      <tbody>
                        {records.map((r, i) => (
                          <tr key={r.id} className={`border-b border-slate-50 transition-colors ${selected.includes(r.id) ? "bg-red-50/60" : "hover:bg-slate-50/70"}`}>
                            <td className="px-4 py-3">
                              <input type="checkbox" checked={selected.includes(r.id)} onChange={() => toggleSelect(r.id)} className="w-4 h-4 accent-emerald-500 cursor-pointer" />
                            </td>
                            <td className="px-4 py-3 text-slate-500">{i + 1}</td>
                            <td className="px-4 py-3 font-semibold text-emerald-700">{r.admNo}</td>
                            <td className="px-4 py-3 text-slate-700 font-medium">{r.name}</td>
                            <td className="px-4 py-3 text-slate-600">{r.class}</td>
                            <td className="px-4 py-3 text-slate-600">{r.date}</td>
                            <td className="px-4 py-3"><StatusBadge status={r.status} /></td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  {/* Mobile Cards */}
                  <div className="md:hidden space-y-3">
                    {/* Select All mobile */}
                    <label className="flex items-center gap-2 text-sm font-semibold text-slate-600 cursor-pointer select-none">
                      <input type="checkbox" checked={selected.length === records.length} onChange={toggleAll} className="w-4 h-4 accent-emerald-500" />
                      Select All ({records.length})
                    </label>
                    {records.map((r, i) => (
                      <div key={r.id} onClick={() => toggleSelect(r.id)} className={`rounded-2xl border p-4 cursor-pointer transition-all select-none ${selected.includes(r.id) ? "border-red-300 bg-red-50/60 shadow-sm" : "border-slate-100 bg-slate-50/50 hover:border-slate-200"}`}>
                        <div className="flex items-start justify-between gap-3">
                          <div className="flex items-center gap-3 min-w-0">
                            <input type="checkbox" checked={selected.includes(r.id)} onChange={() => toggleSelect(r.id)} onClick={(e) => e.stopPropagation()} className="w-4 h-4 accent-emerald-500 flex-shrink-0" />
                            <div className="min-w-0">
                              <p className="font-bold text-slate-800 truncate">{r.name}</p>
                              <p className="text-xs text-emerald-600 font-semibold">{r.admNo}</p>
                            </div>
                          </div>
                          <StatusBadge status={r.status} />
                        </div>
                        <div className="mt-3 flex flex-wrap gap-3 text-xs text-slate-500">
                          <span className="flex items-center gap-1"><BookOpen size={11} />{r.class}</span>
                          <span className="flex items-center gap-1"><Calendar size={11} />{r.date}</span>
                        </div>
                      </div>
                    ))}
                  </div>

                  {/* Delete Button */}
                  <div className="mt-5 flex flex-wrap items-center justify-between gap-3">
                    <p className="text-sm text-slate-500">
                      {selected.length > 0 ? <span className="text-red-500 font-semibold">{selected.length} record(s) selected</span> : "No records selected"}
                    </p>
                    <button
                      onClick={handleDelete}
                      disabled={selected.length === 0}
                      className="flex items-center gap-2 px-5 py-2.5 bg-red-500 hover:bg-red-600 text-white text-sm font-semibold rounded-xl transition-all shadow-sm disabled:opacity-40 disabled:cursor-not-allowed"
                    >
                      <Trash2 size={15} /> Delete Selected
                    </button>
                  </div>
                </>
              )}
            </div>
          </Card>
        )}
      </div>

      <ConfirmModal open={confirmOpen} onConfirm={confirmDelete} onCancel={() => setConfirmOpen(false)} count={selected.length} />
      {toast && <Toast msg={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </>
  );
};

// ─────────────────────────────────────────────
// TAB: TOTAL ATTENDANCE REPORT
// ─────────────────────────────────────────────
const AttendanceReportTab = () => {
  const [session, setSession] = useState("0");
  const [classId, setClassId] = useState("0");
  const [sessionErr, setSessionErr] = useState("");
  const [classErr, setClassErr] = useState("");
  const [reportData, setReportData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [shown, setShown] = useState(false);

  const validate = () => {
    let valid = true;
    if (session === "0") { setSessionErr("Please select a session"); valid = false; } else setSessionErr("");
    if (classId === "0") { setClassErr("Please select a class"); valid = false; } else setClassErr("");
    return valid;
  };

  const handleShow = () => {
    if (!validate()) return;
    setLoading(true);
    setTimeout(() => {
      setReportData(REPORT_DATA);
      setShown(true);
      setLoading(false);
    }, 700);
  };

  const handleReset = () => {
    setSession("0"); setClassId("0");
    setSessionErr(""); setClassErr("");
    setReportData([]); setShown(false);
  };

  const totals = useMemo(() => ({
    total: reportData.reduce((s, r) => s + r.total, 0),
    present: reportData.reduce((s, r) => s + r.present, 0),
    absent: reportData.reduce((s, r) => s + r.absent, 0),
  }), [reportData]);

  const attendancePct = totals.total > 0 ? Math.round((totals.present / totals.total) * 100) : 0;

  return (
    <div className="space-y-5">
      {/* Filter Card */}
      <Card>
        <CardHeader title="Total Attendance Report" icon={BarChart3} subtitle="Select session and class to generate report" />
        <div className="p-5 space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <FormSelect label="Session" id="session" value={session} onChange={(e) => setSession(e.target.value)} options={SESSIONS} error={sessionErr} />
            <FormSelect label="Class" id="classId" value={classId} onChange={(e) => setClassId(e.target.value)} options={CLASSES} error={classErr} />
            <div className="flex flex-col justify-end gap-2">
              <label className="text-xs font-semibold text-slate-600 uppercase tracking-wide invisible">Action</label>
              <div className="flex gap-3">
                <button onClick={handleShow} disabled={loading} className="flex-1 sm:flex-none flex items-center justify-center gap-2 px-5 py-2.5 bg-emerald-500 hover:bg-emerald-600 text-white text-sm font-semibold rounded-xl transition-all shadow-sm disabled:opacity-60">
                  {loading ? <RefreshCw size={15} className="animate-spin" /> : <Eye size={15} />}
                  {loading ? "Loading…" : "Show Report"}
                </button>
                <button onClick={handleReset} className="flex items-center gap-2 px-4 py-2.5 border border-slate-200 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-50 transition-colors">
                  <RefreshCw size={14} />
                </button>
              </div>
            </div>
          </div>
        </div>
      </Card>

      {/* Report Results */}
      {shown && (
        <>
          {/* Summary Stats */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <div className="bg-white rounded-2xl border border-slate-100 p-4 text-center shadow-sm">
              <p className="text-2xl font-extrabold text-slate-800">{totals.total}</p>
              <p className="text-xs text-slate-500 font-semibold mt-1">Total Students</p>
            </div>
            <div className="bg-emerald-50 rounded-2xl border border-emerald-100 p-4 text-center shadow-sm">
              <p className="text-2xl font-extrabold text-emerald-700">{totals.present}</p>
              <p className="text-xs text-emerald-600 font-semibold mt-1">Present</p>
            </div>
            <div className="bg-red-50 rounded-2xl border border-red-100 p-4 text-center shadow-sm">
              <p className="text-2xl font-extrabold text-red-500">{totals.absent}</p>
              <p className="text-xs text-red-500 font-semibold mt-1">Absent</p>
            </div>
            <div className="bg-blue-50 rounded-2xl border border-blue-100 p-4 text-center shadow-sm">
              <p className="text-2xl font-extrabold text-blue-600">{attendancePct}%</p>
              <p className="text-xs text-blue-500 font-semibold mt-1">Attendance</p>
            </div>
          </div>

          {/* Table Card */}
          <Card>
            <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-xl bg-emerald-100 flex items-center justify-center">
                  <BarChart3 className="text-emerald-600" size={18} />
                </div>
                <div>
                  <h2 className="text-base font-bold text-slate-800">Class-wise Report</h2>
                  <p className="text-xs text-slate-500">{reportData.length} classes</p>
                </div>
              </div>
              <button className="hidden sm:flex items-center gap-2 px-4 py-2 border border-slate-200 text-slate-600 text-xs font-semibold rounded-xl hover:bg-slate-50 transition-colors">
                <Download size={13} /> Export
              </button>
            </div>
            <div className="p-5">
              {/* Desktop Table */}
              <div className="hidden md:block rounded-xl overflow-hidden border border-slate-100">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="bg-slate-50 border-b border-slate-100">
                      {["S.No.", "Class", "Total Students", "Present", "Absent", "Attendance %"].map((h) => (
                        <th key={h} className="px-4 py-3 text-left text-xs font-bold text-slate-500 uppercase tracking-wide">{h}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {reportData.map((r, i) => {
                      const pct = Math.round((r.present / r.total) * 100);
                      return (
                        <tr key={r.id} className="border-b border-slate-50 hover:bg-slate-50/70 transition-colors">
                          <td className="px-4 py-3 text-slate-500">{i + 1}</td>
                          <td className="px-4 py-3 font-bold text-slate-800">{r.class}</td>
                          <td className="px-4 py-3">
                            <span className="inline-flex items-center gap-1.5 font-semibold text-slate-700">
                              <Users size={13} className="text-slate-400" />{r.total}
                            </span>
                          </td>
                          <td className="px-4 py-3">
                            <span className="inline-flex items-center gap-1.5 font-semibold text-emerald-700">
                              <UserCheck size={13} />{r.present}
                            </span>
                          </td>
                          <td className="px-4 py-3">
                            <span className="inline-flex items-center gap-1.5 font-semibold text-red-500">
                              <UserX size={13} />{r.absent}
                            </span>
                          </td>
                          <td className="px-4 py-3">
                            <div className="flex items-center gap-2">
                              <div className="flex-1 bg-slate-100 rounded-full h-2 max-w-[80px]">
                                <div className={`h-2 rounded-full transition-all ${pct >= 90 ? "bg-emerald-500" : pct >= 75 ? "bg-amber-400" : "bg-red-400"}`} style={{ width: `${pct}%` }} />
                              </div>
                              <span className={`text-xs font-bold ${pct >= 90 ? "text-emerald-600" : pct >= 75 ? "text-amber-600" : "text-red-500"}`}>{pct}%</span>
                            </div>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                  {/* Footer row */}
                  <tfoot>
                    <tr className="bg-emerald-50 border-t-2 border-emerald-100">
                      <td colSpan={2} className="px-4 py-3 text-xs font-bold text-slate-600 uppercase">Total</td>
                      <td className="px-4 py-3 font-extrabold text-slate-800">{totals.total}</td>
                      <td className="px-4 py-3 font-extrabold text-emerald-700">{totals.present}</td>
                      <td className="px-4 py-3 font-extrabold text-red-500">{totals.absent}</td>
                      <td className="px-4 py-3 font-extrabold text-blue-600">{attendancePct}%</td>
                    </tr>
                  </tfoot>
                </table>
              </div>

              {/* Mobile Cards */}
              <div className="md:hidden space-y-3">
                {reportData.map((r, i) => {
                  const pct = Math.round((r.present / r.total) * 100);
                  return (
                    <div key={r.id} className="rounded-2xl border border-slate-100 bg-slate-50/50 p-4">
                      <div className="flex items-center justify-between mb-3">
                        <div>
                          <p className="font-extrabold text-slate-800">{r.class}</p>
                          <p className="text-xs text-slate-400 mt-0.5">#{i + 1}</p>
                        </div>
                        <span className={`text-xl font-extrabold ${pct >= 90 ? "text-emerald-600" : pct >= 75 ? "text-amber-500" : "text-red-500"}`}>{pct}%</span>
                      </div>
                      {/* Progress bar */}
                      <div className="w-full bg-slate-200 rounded-full h-2 mb-3">
                        <div className={`h-2 rounded-full ${pct >= 90 ? "bg-emerald-500" : pct >= 75 ? "bg-amber-400" : "bg-red-400"}`} style={{ width: `${pct}%` }} />
                      </div>
                      <div className="grid grid-cols-3 gap-2">
                        <StatPill label="Total" value={r.total} color="bg-white border border-slate-100 text-slate-700" />
                        <StatPill label="Present" value={r.present} color="bg-emerald-50 text-emerald-700" />
                        <StatPill label="Absent" value={r.absent} color="bg-red-50 text-red-500" />
                      </div>
                    </div>
                  );
                })}
                {/* Mobile Total Summary */}
                <div className="rounded-2xl bg-emerald-600 text-white p-4">
                  <p className="text-xs font-bold uppercase tracking-wide opacity-80 mb-3">Overall Summary</p>
                  <div className="grid grid-cols-3 gap-3">
                    <div className="text-center"><p className="text-xl font-extrabold">{totals.total}</p><p className="text-xs opacity-75">Total</p></div>
                    <div className="text-center"><p className="text-xl font-extrabold">{totals.present}</p><p className="text-xs opacity-75">Present</p></div>
                    <div className="text-center"><p className="text-xl font-extrabold">{totals.absent}</p><p className="text-xs opacity-75">Absent</p></div>
                  </div>
                </div>
              </div>

              {/* Export mobile */}
              <div className="md:hidden mt-4">
                <button className="w-full flex items-center justify-center gap-2 px-4 py-3 border border-slate-200 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-50 transition-colors">
                  <Download size={15} /> Export Report
                </button>
              </div>
            </div>
          </Card>
        </>
      )}
    </div>
  );
};

// ─────────────────────────────────────────────
// MAIN PAGE
// ─────────────────────────────────────────────
const TABS = [
  { key: "delete", label: "Delete Attendance", icon: Trash2 },
  { key: "report", label: "Attendance Report", icon: BarChart3 },
];

export default function StudentAttendancePage() {
  const [activeTab, setActiveTab] = useState("delete");

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-emerald-50/30 font-sans">
      {/* Top Nav Bar */}
      <header className="sticky top-0 z-40 bg-white/80 backdrop-blur-md border-b border-slate-100 shadow-sm">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 flex items-center justify-between h-14">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-xl bg-emerald-500 flex items-center justify-center shadow">
              <BookOpen className="text-white" size={16} />
            </div>
            <span className="font-extrabold text-slate-800 text-base tracking-tight hidden sm:block">EduManage</span>
            <span className="text-slate-300 hidden sm:block">|</span>
            <span className="text-slate-500 text-sm hidden sm:block">Attendance</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-full bg-emerald-100 flex items-center justify-center text-emerald-600 text-xs font-bold">AD</div>
          </div>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-4 sm:px-6 py-6 sm:py-8">
        <Breadcrumb items={["Home", activeTab === "delete" ? "Delete Attendance" : "Total Attendance Report"]} />

        {/* Tab Switcher */}
        <div className="flex bg-white border border-slate-100 rounded-2xl p-1 shadow-sm mb-6 w-full sm:w-auto sm:inline-flex gap-1">
          {TABS.map((tab) => {
            const Icon = tab.icon;
            const active = activeTab === tab.key;
            return (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className={`flex-1 sm:flex-none flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold transition-all ${active ? "bg-emerald-500 text-white shadow-sm" : "text-slate-500 hover:text-slate-700 hover:bg-slate-50"}`}
              >
                <Icon size={15} />
                <span className="hidden xs:inline sm:inline">{tab.label}</span>
                <span className="xs:hidden sm:hidden">{tab.key === "delete" ? "Delete" : "Report"}</span>
              </button>
            );
          })}
        </div>

        {/* Tab Content */}
        {activeTab === "delete" ? <DeleteAttendanceTab /> : <AttendanceReportTab />}
      </main>
    </div>
  );
}
