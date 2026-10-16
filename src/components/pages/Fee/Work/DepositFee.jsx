/**
 * DepositFee.jsx
 * Folder: src/pages/Fee/DepositFee.jsx
 *
 * Full conversion of DepositFee.aspx → React + Tailwind CSS (Vite, JS only)
 *
 * Tabs:
 *  1. Deposit Fee      — installment grid, payment row, MOP cheque grid, adjust & deposit
 *  2. Fee Summary      — session-wise head-wise summary (accordion)
 *  3. Previous Txn     — receipt history (accordion), print / edit / delete
 *  4. Deposit Misc Fee — misc head grid + cheque grid + deposit
 *  5. Assign Optional  — fee-head selector + student checkbox grid
 *
 * Mobile: all wide tables → cards / stacked layouts, sticky top bar, bottom sheet filters
 * Desktop: dense ERP grid layout
 */

import { useState, useMemo, useCallback, useRef, useEffect } from 'react'
import { apiFetch } from '../../../../lib/api'
import {
  Search, ChevronDown, ChevronUp, ChevronRight,
  RefreshCw, Eye, Printer, Pencil, Trash2,
  Plus, Minus, Check, X, AlertCircle, Loader2,
  CreditCard, Banknote, Building2, Smartphone,
  IndianRupee, Calendar, User, Phone, MapPin,
  FileSpreadsheet, Download, MoreVertical, Clock,
  Info, CheckSquare, Square, ArrowRight, Hash,
  BookOpen, BarChart3, Settings, Star, Zap,
  TrendingUp, TrendingDown, Shield, Bus, Monitor,
  ChevronLeft, Menu, Bell, Home, DollarSign
} from 'lucide-react'

// ─── DUMMY DATA ─────────────────────────────────────────────────────────────

const FEE_SUMMARY = [
  {
    session: '2025-26', class_id: '13', stu_id: 'S001', expanded: false,
    heads: [
      { installment_no: 'April',  headName: 'Tuition Fee',  head_amount: 8500, concession: 500, receive_amount: 8000, balance: 0    },
      { installment_no: 'April',  headName: 'Library Fee',  head_amount: 500,  concession: 0,   receive_amount: 500,  balance: 0    },
      { installment_no: 'May',    headName: 'Tuition Fee',  head_amount: 8500, concession: 500, receive_amount: 0,    balance: 8000 },
      { installment_no: 'May',    headName: 'Library Fee',  head_amount: 500,  concession: 0,   receive_amount: 0,    balance: 500  },
      { installment_no: 'June',   headName: 'Tuition Fee',  head_amount: 8500, concession: 500, receive_amount: 8000, balance: 0    },
    ]
  },
  {
    session: '2024-25', class_id: '13', stu_id: 'S001', expanded: false,
    heads: [
      { installment_no: 'April',  headName: 'Tuition Fee',  head_amount: 7800, concession: 400, receive_amount: 7400, balance: 0    },
      { installment_no: 'May',    headName: 'Tuition Fee',  head_amount: 7800, concession: 400, receive_amount: 7400, balance: 0    },
    ]
  }
]

const PREV_TRANSACTIONS = [
  {
    session: '2025-26', class_id: '13', stu_id: 'S001', expanded: false,
    txns: [
      { id: 1, inst_no: 'April-June', receipt_no: 'RC-2024-001', paymentdate: '05 Apr 2025', mop: 'Cash',       transaction_no: '',          transaction_date: '',          installment_amount: 24500, latefee: 0,   advance_amount: 0,   amount: 24500, status: 'active',   reconsil_BY: '' },
      { id: 2, inst_no: 'April',      receipt_no: 'RC-2024-002', paymentdate: '10 May 2025', mop: 'Cheque',     transaction_no: 'CHQ-556677', transaction_date: '08 May 2025', installment_amount: 8000,  latefee: 200, advance_amount: 0,   amount: 8200,  status: 'inactive', reconsil_BY: 'Admin' },
      { id: 3, inst_no: 'July',       receipt_no: 'RC-2024-003', paymentdate: '02 Jul 2025', mop: 'Net Banking', transaction_no: 'TXN-889900', transaction_date: '02 Jul 2025', installment_amount: 8000,  latefee: 0,   advance_amount: 500, amount: 8500,  status: 'active',   reconsil_BY: '' },
    ]
  },
  {
    session: '2024-25', class_id: '13', stu_id: 'S001', expanded: false,
    txns: [
      { id: 4, inst_no: 'April',      receipt_no: 'RC-2023-001', paymentdate: '04 Apr 2024', mop: 'Cash',       transaction_no: '',          transaction_date: '',          installment_amount: 7400,  latefee: 0,   advance_amount: 0,   amount: 7400,  status: 'active',   reconsil_BY: '' },
    ]
  }
]

const MISC_HEADS = [
  { id: 1, name: 'Late Fee' }, { id: 2, name: 'Duplicate Certificate' },
  { id: 3, name: 'Sports Fee' }, { id: 4, name: 'Lab Fee' }, { id: 5, name: 'Exam Fee' },
]

const OPTIONAL_HEADS = [
  { id: 1, name: 'Computer Science' }, { id: 2, name: 'Physical Education' },
  { id: 3, name: 'Biology' }, { id: 4, name: 'Mathematics' },
]

const OPTIONAL_STUDENTS = [
  { id: 'S001', admNo: '13-6114', name: 'Aryan Sharma',   class_id: '13', sts: true,  installments: ['April', 'May', 'June', 'July', 'August', 'September'] },
  { id: 'S002', admNo: '13-6218', name: 'Priya Verma',    class_id: '13', sts: false, installments: ['April', 'May', 'June', 'July', 'August', 'September'] },
  { id: 'S003', admNo: '13-6301', name: 'Rahul Gupta',    class_id: '14', sts: true,  installments: ['April', 'May', 'June', 'July', 'August', 'September'] },
  { id: 'S004', admNo: '13-6445', name: 'Sneha Patel',    class_id: '12', sts: false, installments: ['April', 'May', 'June', 'July', 'August', 'September'] },
]

const mopIcon = (mop) => {
  if (!mop) return <Banknote className="w-3.5 h-3.5" />
  const m = mop.toLowerCase()
  if (m.includes('cash'))    return <Banknote className="w-3.5 h-3.5" />
  if (m.includes('cheque'))  return <FileSpreadsheet className="w-3.5 h-3.5" />
  if (m.includes('net') || m.includes('neft')) return <Building2 className="w-3.5 h-3.5" />
  if (m.includes('mobile') || m.includes('online')) return <Smartphone className="w-3.5 h-3.5" />
  return <CreditCard className="w-3.5 h-3.5" />
}

// ─── TINY PRIMITIVES ─────────────────────────────────────────────────────────

function Badge({ children, color = 'blue', size = 'sm' }) {
  const colors = {
    blue:    'bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-300',
    green:   'bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-300',
    red:     'bg-rose-100 text-rose-700 dark:bg-rose-500/15 dark:text-rose-300',
    amber:   'bg-amber-100 text-amber-700 dark:bg-amber-500/15 dark:text-amber-400',
    violet:  'bg-violet-100 text-violet-700 dark:bg-violet-500/15 dark:text-violet-300',
    slate:   'bg-slate-100 text-slate-600 dark:bg-slate-700 dark:text-slate-300',
  }
  const sizes = { sm: 'text-[11px] px-2 py-0.5', xs: 'text-[10px] px-1.5 py-px' }
  return (
    <span className={`inline-flex items-center rounded-full font-semibold ${colors[color]} ${sizes[size]}`}>
      {children}
    </span>
  )
}

function Pill({ label, value, color = 'blue' }) {
  const bg = {
    blue:   'from-blue-600 to-blue-500',
    green:  'from-emerald-600 to-emerald-500',
    amber:  'from-amber-500 to-amber-400',
    red:    'from-rose-600 to-rose-500',
    slate:  'from-slate-600 to-slate-500',
  }
  return (
    <div className="flex flex-col items-center">
      <p className="text-[11px] text-slate-400 dark:text-slate-500 mb-0.5 whitespace-nowrap">{label}</p>
      <div className={`flex items-center gap-1 px-3 py-1 rounded-xl bg-gradient-to-br ${bg[color]} text-white text-[13px] font-bold tabular-nums shadow-sm`}>
        <IndianRupee className="w-3 h-3 opacity-75" />
        {Number(value || 0).toLocaleString('en-IN')}
      </div>
    </div>
  )
}

function AmtDisplay({ value, color = 'default' }) {
  const cls = {
    default: 'text-slate-700 dark:text-slate-200',
    red:     'text-rose-600 dark:text-rose-400',
    green:   'text-emerald-600 dark:text-emerald-400',
    amber:   'text-amber-600 dark:text-amber-400',
    black:   'text-slate-900 dark:text-slate-100',
  }
  return (
    <span className={`font-semibold tabular-nums text-[13px] ${cls[color]}`}>
      {Number(value || 0).toLocaleString('en-IN')}
    </span>
  )
}

function NativeSelect({ value, onChange, children, placeholder, error, disabled = false, className = '' }) {
  return (
    <div className="relative">
      <select
        value={value}
        onChange={onChange}
        disabled={disabled}
        className={`w-full appearance-none pl-3 pr-8 py-2 text-[13px] rounded-lg border outline-none transition-all bg-white text-slate-800 dark:bg-[#1e2238] dark:text-slate-200
          ${error ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)] focus:border-blue-400 focus:ring-2 focus:ring-blue-100 dark:focus:border-indigo-400'}
          ${className}`}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {children}
      </select>
      <ChevronDown className="absolute right-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
    </div>
  )
}

function Input({ value, onChange, placeholder, disabled, className = '', type = 'text' }) {
  return (
    <input
      type={type}
      value={value}
      onChange={onChange}
      placeholder={placeholder}
      disabled={disabled}
      className={`w-full px-3 py-2 text-[13px] rounded-lg border outline-none transition-all
        bg-white text-slate-800 border-slate-200 placeholder-slate-300
        focus:border-blue-400 focus:ring-2 focus:ring-blue-100
        dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
        dark:placeholder-slate-600 dark:focus:border-indigo-400
        disabled:bg-slate-50 dark:disabled:bg-[#191c2a] disabled:text-slate-400 disabled:cursor-not-allowed
        ${className}`}
    />
  )
}

function Btn({ children, onClick, variant = 'primary', size = 'md', disabled, className = '', icon: Icon }) {
  const variants = {
    primary:   'bg-blue-600 hover:bg-blue-700 text-white shadow-md shadow-blue-500/20 dark:bg-indigo-600 dark:hover:bg-indigo-700 dark:shadow-indigo-500/20',
    success:   'bg-emerald-600 hover:bg-emerald-700 text-white shadow-md shadow-emerald-500/20',
    danger:    'bg-rose-500 hover:bg-rose-600 text-white shadow-md shadow-rose-500/20',
    amber:     'bg-amber-500 hover:bg-amber-600 text-white shadow-md shadow-amber-500/20',
    ghost:     'bg-slate-100 hover:bg-slate-200 text-slate-700 dark:bg-slate-800 dark:hover:bg-slate-700 dark:text-slate-200',
    outline:   'border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800/50 text-slate-700 dark:text-slate-200',
  }
  const sizes = {
    xs:  'px-2.5 py-1 text-[11px] rounded-lg gap-1',
    sm:  'px-3 py-1.5 text-[12px] rounded-lg gap-1.5',
    md:  'px-4 py-2 text-[13px] rounded-xl gap-2',
    lg:  'px-5 py-2.5 text-[14px] rounded-xl gap-2',
  }
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      className={`inline-flex items-center justify-center font-semibold transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed ${variants[variant]} ${sizes[size]} ${className}`}
    >
      {Icon && <Icon className="w-3.5 h-3.5 flex-shrink-0" />}
      {children}
    </button>
  )
}

function Checkbox({ checked, onChange, label, className = '' }) {
  return (
    <label className={`inline-flex items-center gap-2 cursor-pointer select-none ${className}`}>
      <span
        onClick={onChange}
        className={`w-4 h-4 rounded border-2 flex-shrink-0 flex items-center justify-center transition-all cursor-pointer
          ${checked
            ? 'bg-blue-600 border-blue-600 dark:bg-indigo-500 dark:border-indigo-500'
            : 'border-slate-300 dark:border-slate-600 hover:border-blue-400'}`}
      >
        {checked && <Check className="w-2.5 h-2.5 text-white" strokeWidth={3} />}
      </span>
      {label && <span className="text-[12px] text-slate-600 dark:text-slate-300 leading-none">{label}</span>}
    </label>
  )
}

function SectionCard({ children, className = '' }) {
  return (
    <div className={`rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden ${className}`}>
      {children}
    </div>
  )
}

function SectionHeader({ icon: Icon, title, subtitle, actions, color = 'blue' }) {
  const colors = { blue: 'bg-blue-500', emerald: 'bg-emerald-500', amber: 'bg-amber-500', violet: 'bg-violet-500' }
  return (
    <div className="flex items-center justify-between gap-3 px-4 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.12)] bg-slate-50/60 dark:bg-white/[0.02]">
      <div className="flex items-center gap-2.5 min-w-0">
        <span className={`w-1 h-5 rounded-full flex-shrink-0 ${colors[color]}`} />
        {Icon && <Icon className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />}
        <div className="min-w-0">
          <p className="text-[13px] font-bold text-slate-700 dark:text-slate-200 leading-tight">{title}</p>
          {subtitle && <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">{subtitle}</p>}
        </div>
      </div>
      {actions && <div className="flex items-center gap-2 flex-shrink-0">{actions}</div>}
    </div>
  )
}

function Toast({ message, type = 'success', onClose }) {
  const bg = type === 'success' ? 'bg-emerald-600' : type === 'error' ? 'bg-rose-600' : 'bg-blue-600'
  const Icon = type === 'success' ? Check : type === 'error' ? AlertCircle : Info
  return (
    <div className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 px-5 py-3 rounded-2xl shadow-2xl text-[13px] font-semibold min-w-[260px] max-w-[90vw] ${bg} text-white`}
      style={{ animation: 'slideUp .25s ease' }}>
      <style>{`@keyframes slideUp{from{opacity:0;transform:translateX(-50%) translateY(10px)}to{opacity:1;transform:translateX(-50%) translateY(0)}}`}</style>
      <Icon className="w-4 h-4 flex-shrink-0" />
      <span className="flex-1">{message}</span>
      <button onClick={onClose}><X className="w-4 h-4 opacity-75" /></button>
    </div>
  )
}

// ─── STUDENT HEADER CARD ─────────────────────────────────────────────────────

function StudentHeader({ student, feeData, session, onPageClick }) {
  if (!student) return null
  const initials = student.name.split(' ').slice(0, 2).map(w => w[0]).join('')
  const current = feeData?.current || [], previous = feeData?.previous || []
  const balance = [...current, ...previous].reduce((sum, row) => sum + Number(row.balance || 0) + Number(row.lateFee || 0), 0)
  const paid = [...current, ...previous].reduce((sum, row) => sum + Number(row.receiveAmount || 0), 0)
  const totalDue = balance + paid
  const goToFeePage = (group, page) => {
    sessionStorage.setItem('fee.selectedStudent', JSON.stringify({
      id: student.id,
      admNo: student.admNo,
      session: session || '',
      name: student.name,
    }))
    onPageClick?.('fee', group, page)
  }

  return (
    <SectionCard>
      {/* Top row: photo + info + stats */}
      <div className="flex flex-col sm:flex-row">
        {/* Left: student info */}
        <div className="flex items-start gap-3 p-4 flex-1 min-w-0 border-b sm:border-b-0 sm:border-r border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          {/* Avatar */}
          <div className="w-14 h-14 sm:w-16 sm:h-16 rounded-2xl bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white text-[18px] font-bold flex-shrink-0 shadow-lg shadow-blue-500/20">
            {initials}
          </div>
          <div className="min-w-0 flex-1">
            <div className="flex items-start gap-2 flex-wrap">
              <h3 className="text-[15px] font-bold text-slate-800 dark:text-slate-100 leading-tight">{student.name}</h3>
              <Badge color={student.status === 'Active' ? 'green' : 'red'} size="xs">{student.status}</Badge>
            </div>
            <p className="text-[12px] text-slate-500 dark:text-slate-400 mt-0.5">
              {student.admNo} · {student.class}
            </p>
            <div className="flex items-center gap-1 mt-1">
              <Phone className="w-3 h-3 text-slate-400" />
              <span className="text-[12px] text-slate-500 dark:text-slate-400">{student.mobile}</span>
            </div>
            <div className="flex items-center gap-3 mt-2 flex-wrap">
              {student.transport !== 'Not Assigned' && (
                <div className="flex items-center gap-1">
                  <Bus className="w-3 h-3 text-amber-500" />
                  <span className="text-[11px] text-amber-600 dark:text-amber-400 font-medium">{student.transport}</span>
                </div>
              )}
              {student.optional && (
                <div className="flex items-center gap-1">
                  <BookOpen className="w-3 h-3 text-violet-500" />
                  <span className="text-[11px] text-violet-600 dark:text-violet-400 font-medium">{student.optional}</span>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Right: financial summary pills */}
        <div className="flex items-center justify-around gap-2 p-4 sm:px-6 flex-wrap sm:flex-nowrap sm:gap-4">
          <Pill label="Balance"      value={balance} color="red"   />
          <Pill label="Paid"         value={paid} color="green" />
          <Pill label="Total Due"    value={totalDue} color="amber" />
          <Pill label="Excess/Adv"   value={feeData?.advance || 0} color="slate" />
        </div>
      </div>

      {/* Bottom bar */}
      <div className="flex items-center justify-between gap-3 px-4 py-2.5 bg-slate-50/60 dark:bg-white/[0.02] border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
        <div className="flex items-center gap-2 flex-wrap">
          <span className="text-[11px] text-slate-400">Adm Date:</span>
          <span className="text-[11px] font-semibold text-slate-600 dark:text-slate-300">{student.admDate}</span>
          <span className="text-[11px] text-slate-400 ml-2">Father:</span>
          <span className="text-[11px] font-semibold text-slate-600 dark:text-slate-300">{student.father}</span>
        </div>
        <div className="flex items-center gap-1.5 flex-wrap justify-end">
          <Btn variant="amber" size="xs" icon={CreditCard} onClick={() => goToFeePage('fee_report', 'StudentFeeSummary')}>Pay Online</Btn>
          <Btn variant="ghost" size="xs" onClick={() => goToFeePage('fee_update', 'ReconFee')}>Cheque bounce</Btn>
          <Btn variant="ghost" size="xs" onClick={() => goToFeePage('fee_setup', 'DefineConcession')}>Concession</Btn>
          <Btn variant="ghost" size="xs" onClick={() => goToFeePage('fee_update', 'AssignConGroup')}>Concession Group</Btn>
          <Btn variant="ghost" size="xs" onClick={() => goToFeePage('fee_work', 'WaveOffFees')}>WaveOff Fees</Btn>
          <Btn variant="ghost" size="xs" onClick={() => goToFeePage('fee_work', 'WaveOffLateFees')}>WaveOff Late Fees</Btn>
        </div>
      </div>
    </SectionCard>
  )
}

// ─── TAB 1: DEPOSIT FEE ──────────────────────────────────────────────────────

function DepositFeeTab({ student, showToast, feeData, session, feeType, bootstrap }) {
  const [prevDues, setPrevDues] = useState([])
  const [currDues, setCurrDues] = useState([])
  const [payDate, setPayDate]   = useState('')
  const [mopRows, setMopRows]   = useState([
     { id: 1, mop: 'Cash', chqNo: '', chqDate: '', settlementDate: '', bank: '', amount: '', useAmount: '', isLast: true }
  ])
  const [advance, setAdvance]   = useState(0)
  const [adjAmt,  setAdjAmt]    = useState(0)
  const [step,    setStep]      = useState(1) // mobile step 1=select dues, 2=payment
  const [deposited, setDeposited] = useState(false)

  useEffect(() => {
    setPrevDues((feeData?.previous || []).map(row => ({ ...row,
      inst_no: row.instNo ?? row.inst_no ?? row.installmentNo ?? '',
      balance: Number(row.balance || row.finalCharges || 0),
      con_charge: Number(row.concession || 0),
      latefee: Number(row.lateFee || row.latefee || 0),
      lateFeeInput: Number(row.lateFee || row.latefee || 0),
      selected: false, waiveOff: false, source: 'previous'
    })))
    setCurrDues((feeData?.current || []).map(row => ({ ...row,
      inst_no: row.instNo ?? row.inst_no ?? row.installmentNo ?? '',
      fee_charges: Number(row.headAmount || row.head_amount || row.payable || 0),
      con_charge: Number(row.concession || row.con_charge || 0),
      WaveOffFeeValue: Number(row.waveOffFeeValue || row.WaveOffFeeValue || 0),
      receive_amount: Number(row.receiveAmount || row.receive_amount || 0),
      latefee: Number(row.lateFee || row.latefee || 0),
      lateFeeInput: Number(row.lateFee || row.latefee || 0),
      balance: Number(row.balance || row.dueAmount || 0),
      waiveOff: false, selected: false, source: 'current'
    })))
    setAdvance(Number(feeData?.advance || 0))
    setDeposited(false)
    setStep(1)
  }, [feeData, student])

  // select / deselect
  const togglePrev = useCallback((id) => {
    setPrevDues(d => d.map(r => r.id === id ? { ...r, selected: !r.selected } : r))
  }, [])
  const toggleCurr = useCallback((id) => {
    setCurrDues(d => d.map(r => r.id === id ? { ...r, selected: !r.selected } : r))
  }, [])
  const toggleAllCurr = useCallback((v) => {
    setCurrDues(d => d.map(r => ({ ...r, selected: v })))
  }, [])
  const toggleAllPrev = useCallback((v) => {
    setPrevDues(d => d.map(r => ({ ...r, selected: v })))
  }, [])

  // totals
  const prevTotal = useMemo(() =>
    prevDues.filter(r => r.selected).reduce((s, r) => s + r.balance + (r.waiveOff ? Number(r.lateFeeInput ?? r.latefee ?? 0) : r.latefee), 0)
  , [prevDues])

  const currTotal = useMemo(() =>
    currDues.filter(r => r.selected).reduce((s, r) => s + r.balance + (r.waiveOff ? Number(r.lateFeeInput ?? r.latefee ?? 0) : r.latefee), 0)
  , [currDues])

  const totalPayable  = prevTotal + currTotal
  const netPayable    = Math.max(0, totalPayable - adjAmt)
  const mopTotal      = mopRows.reduce((s, r) => s + (parseFloat(r.useAmount || r.amount) || 0), 0)

  const handleDeposit = () => {
    if (!payDate) { showToast('Payment date is required!', 'error'); return }
    if (netPayable === 0) { showToast('No amount to deposit. Select installments.', 'error'); return }
    if (mopTotal < netPayable) { showToast('Payment amount is less than net payable.', 'error'); return }
    if (mopTotal > netPayable && !window.confirm('The deposit amount exceeds the payable amount. Are you sure you want to proceed?')) return
    const dues = [...prevDues, ...currDues].filter(row => row.selected).map(row => ({ id: row.id, source: row.source, session: row.session || session, balance: Number(row.balance || 0), baseAmount: Number(row.balance || 0), lateFee: Number(row.waiveOff ? row.lateFeeInput ?? row.latefee ?? 0 : row.latefee || 0), waiveOff: row.waiveOff }))
    const payments = mopRows.map(row => ({ mop: row.mop, bankId: row.bank, amount: Number(row.amount || 0), useAmount: Number(row.useAmount || row.amount || 0), transactionNo: row.chqNo, transactionDate: row.chqDate, settlementDate: row.settlementDate || '' }))
    apiFetch('/fees/deposit', { method: 'POST', body: JSON.stringify({ studentId: student.id || student.studentId, session, feeType, paymentDate: payDate, advanceAdjusted: Number(adjAmt || 0), payments, dues }) }).then(result => { setDeposited(true); showToast(`Fee deposited successfully. Receipt ${result.receiptNo || ''}`, 'success') }).catch(error => showToast(error.message || 'Fee deposit failed', 'error'))
  }

  const addMopRow = () => {
    setMopRows(r => [...r.map(x => ({ ...x, isLast: false })), { id: Date.now(), mop: 'Cash', chqNo: '', chqDate: '', settlementDate: '', bank: '', amount: '', useAmount: '', isLast: true }])
  }
  const removeMopRow = (id) => {
    setMopRows(r => r.filter(x => x.id !== id))
  }
  const updateMop = (id, key, val) => {
    setMopRows(rows => rows.map(row => {
      if (row.id !== id) return row
      if (key !== 'mop') return { ...row, [key]: val }
      const next = { ...row, mop: val }
      if (val === 'Cash' || val === 'Select') {
        next.bank = ''; next.chqNo = ''; next.chqDate = ''; next.settlementDate = ''
      } else if (val !== 'Online') {
        next.settlementDate = ''
      }
      if (val === 'Select') { next.amount = ''; next.useAmount = '' }
      return next
    }))
  }

  const updateLateFee = (source, id, value) => {
    const setter = source === 'previous' ? setPrevDues : setCurrDues
    setter(rows => rows.map(row => row.id === id ? { ...row, lateFeeInput: Math.max(0, Math.min(Number(row.latefee || 0), Number(value) || 0)) } : row))
  }

  const allCurrSelected = currDues.length > 0 && currDues.every(r => r.selected)
  const allPrevSelected = prevDues.length > 0 && prevDues.every(r => r.selected)

  return (
    <div className="space-y-4">

      {/* ── MOBILE STEP INDICATOR ── */}
      <div className="flex sm:hidden items-center bg-white dark:bg-[#1a1f35] rounded-2xl border border-slate-100 dark:border-[rgba(99,102,241,0.15)] overflow-hidden shadow-sm">
        {[['1', 'Select Dues'], ['2', 'Payment']].map(([n, label], i) => (
          <button
            key={n}
            onClick={() => setStep(Number(n))}
            className={`flex-1 flex items-center justify-center gap-2 py-3 text-[12px] font-semibold transition-colors
              ${step === Number(n)
                ? 'bg-blue-600 dark:bg-indigo-600 text-white'
                : 'text-slate-400 dark:text-slate-500 hover:text-slate-600'}`}
          >
            <span className={`w-5 h-5 rounded-full text-[11px] font-bold flex items-center justify-center
              ${step === Number(n) ? 'bg-white/25 text-white' : 'bg-slate-100 dark:bg-slate-800'}`}>{n}</span>
            {label}
          </button>
        ))}
      </div>

      {/* ── PREVIOUS DUES ── (show on step 1 mobile / always desktop) */}
      <div className={step === 1 ? '' : 'hidden sm:block'}>
        <SectionCard className="hidden-on-mobile-step2">
          <SectionHeader icon={Clock} title="Previous Session Dues" color="amber"
            actions={
              <Checkbox checked={allPrevSelected} onChange={() => toggleAllPrev(!allPrevSelected)} label="Select All" />
            }
          />
          {/* Desktop table */}
          <div className="hidden sm:block overflow-x-auto">
            <table className="w-full">
              <thead className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/40 dark:bg-white/[0.02]">
                <tr>
                  {['', 'Session', 'Previous Due', 'Concession', 'Late Fee', 'Waive Off', 'Remark'].map((h, i) => (
                    <th key={i} className="px-3 py-2.5 text-[11px] font-bold uppercase tracking-wide text-left text-slate-500 dark:text-slate-400 whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {prevDues.map(row => (
                  <tr key={row.id} className={`border-b border-slate-100 dark:border-[rgba(99,102,241,0.06)] transition-colors ${row.selected ? 'bg-amber-50/40 dark:bg-amber-500/5' : 'hover:bg-slate-50/50 dark:hover:bg-white/[0.02]'}`}>
                    <td className="px-3 py-2.5"><Checkbox checked={row.selected} onChange={() => togglePrev(row.id)} /></td>
                    <td className="px-3 py-2.5"><Badge color="amber">{row.session}</Badge></td>
                    <td className="px-3 py-2.5"><AmtDisplay value={row.balance} color="red" /></td>
                    <td className="px-3 py-2.5"><AmtDisplay value={row.con_charge} /></td>
                    <td className="px-3 py-2.5">{row.waiveOff
                      ? <Input value={row.lateFeeInput} onChange={e => updateLateFee('previous', row.id, e.target.value)} type="number" className="w-24" />
                      : <AmtDisplay value={row.latefee} color="amber" />}</td>
                    <td className="px-3 py-2.5">
                      <Checkbox checked={row.waiveOff} onChange={() => setPrevDues(d => d.map(r => r.id === row.id ? { ...r, waiveOff: !r.waiveOff } : r))} label="Waive" />
                    </td>
                    <td className="px-3 py-2.5">
                      <Input value={row.remark || ''} onChange={e => setPrevDues(d => d.map(r => r.id === row.id ? { ...r, remark: e.target.value } : r))} placeholder="Remark..." />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {/* Mobile cards */}
          <div className="sm:hidden p-3 space-y-2">
            {prevDues.map(row => (
              <div key={row.id} onClick={() => togglePrev(row.id)}
                className={`rounded-xl border-2 p-3 transition-all cursor-pointer
                  ${row.selected ? 'border-amber-400 bg-amber-50 dark:bg-amber-500/8' : 'border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1e2238]'}`}>
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center gap-2">
                    <Checkbox checked={row.selected} onChange={() => togglePrev(row.id)} />
                    <Badge color="amber">{row.session}</Badge>
                  </div>
                  <AmtDisplay value={row.balance} color="red" />
                </div>
                <div className="grid grid-cols-2 gap-2 text-[11px]">
                  <div><span className="text-slate-400">Concession:</span> <span className="font-semibold text-slate-600 dark:text-slate-300">{row.con_charge}</span></div>
                   <div><span className="text-slate-400">Late Fee:</span> {row.waiveOff
                     ? <Input value={row.lateFeeInput} onChange={e => { e.stopPropagation(); updateLateFee('previous', row.id, e.target.value) }} type="number" className="w-20 inline-flex ml-1" />
                     : <span className="font-semibold text-amber-600">{row.latefee}</span>}</div>
                </div>
                <div className="mt-2 flex gap-2">
                  <Checkbox checked={row.waiveOff} onChange={e => { e.stopPropagation(); setPrevDues(d => d.map(r => r.id === row.id ? { ...r, waiveOff: !r.waiveOff } : r)) }} label="Waive Off Late Fee" />
                </div>
              </div>
            ))}
          </div>
        </SectionCard>
      </div>

      {/* ── CURRENT DUES GRID ── */}
      {(step === 1) && (
        <SectionCard>
          <SectionHeader icon={CreditCard} title="Current Session Dues" color="blue"
            actions={
              <Checkbox checked={allCurrSelected} onChange={() => toggleAllCurr(!allCurrSelected)} label="Select All" />
            }
          />
          {/* Desktop table */}
          <div className="hidden sm:block overflow-x-auto">
            <table className="w-full">
              <thead className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/40 dark:bg-white/[0.02]">
                <tr>
                  {['', 'Installment', 'Head Amt', 'Concession', 'Waive Off', 'Rec. Amt', 'Late Fee', 'Balance', 'Waive Late', 'Remark'].map((h, i) => (
                    <th key={i} className="px-3 py-2.5 text-[11px] font-bold uppercase tracking-wide text-left text-slate-500 dark:text-slate-400 whitespace-nowrap">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {currDues.map(row => (
                  <tr key={row.id} className={`border-b border-slate-100 dark:border-[rgba(99,102,241,0.06)] transition-colors ${row.selected ? 'bg-blue-50/50 dark:bg-blue-500/5' : 'hover:bg-slate-50/50 dark:hover:bg-white/[0.02]'}`}>
                    <td className="px-3 py-2.5"><Checkbox checked={row.selected} onChange={() => toggleCurr(row.id)} /></td>
                    <td className="px-3 py-2.5">
                      <span className={`inline-flex items-center px-2 py-1 rounded-lg text-[12px] font-semibold
                        ${row.balance === 0 ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-300' : 'bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-300'}`}>
                        {row.balance === 0 && <Check className="w-3 h-3 mr-1" />}
                        {row.inst_no}
                      </span>
                    </td>
                    <td className="px-3 py-2.5"><AmtDisplay value={row.fee_charges} /></td>
                    <td className="px-3 py-2.5"><AmtDisplay value={row.con_charge} color="green" /></td>
                    <td className="px-3 py-2.5"><AmtDisplay value={row.WaveOffFeeValue} /></td>
                    <td className="px-3 py-2.5"><AmtDisplay value={row.receive_amount} color="green" /></td>
                    <td className="px-3 py-2.5">{row.waiveOff
                      ? <Input value={row.lateFeeInput} onChange={e => updateLateFee('current', row.id, e.target.value)} type="number" className="w-24" />
                      : <AmtDisplay value={row.latefee} color="amber" />}</td>
                    <td className="px-3 py-2.5">
                      <span className={`font-bold text-[13px] tabular-nums ${row.balance === 0 ? 'text-emerald-600' : 'text-rose-600'}`}>{row.balance}</span>
                    </td>
                    <td className="px-3 py-2.5">
                      <Checkbox checked={row.waiveOff} onChange={() => setCurrDues(d => d.map(r => r.id === row.id ? { ...r, waiveOff: !r.waiveOff } : r))} label="Waive" />
                    </td>
                    <td className="px-3 py-2.5">
                      <Input className="w-28" value={row.remark || ''} onChange={e => setCurrDues(d => d.map(r => r.id === row.id ? { ...r, remark: e.target.value } : r))} placeholder="Remark" />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {/* Mobile cards */}
          <div className="sm:hidden p-3 space-y-2">
            {currDues.map(row => (
              <div key={row.id} onClick={() => toggleCurr(row.id)}
                className={`rounded-xl border-2 p-3 cursor-pointer transition-all
                  ${row.selected ? 'border-blue-400 bg-blue-50 dark:bg-blue-500/8' : 'border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1e2238]'}
                  ${row.balance === 0 ? 'opacity-60' : ''}`}>
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center gap-2">
                    <Checkbox checked={row.selected} onChange={() => toggleCurr(row.id)} />
                    <span className={`text-[13px] font-bold ${row.balance === 0 ? 'text-emerald-600' : 'text-slate-800 dark:text-slate-100'}`}>
                      {row.balance === 0 && <Check className="w-3.5 h-3.5 inline mr-1" />}
                      {row.inst_no}
                    </span>
                  </div>
                  {row.balance === 0
                    ? <Badge color="green" size="xs">Paid</Badge>
                    : <span className="text-[15px] font-bold text-rose-600 tabular-nums">₹{row.balance.toLocaleString('en-IN')}</span>
                  }
                </div>
                {row.balance > 0 && (
                  <div className="grid grid-cols-3 gap-1 text-[11px] mt-1">
                    <div><span className="text-slate-400">Head:</span> <span className="font-semibold">{row.fee_charges}</span></div>
                    <div><span className="text-slate-400">Con:</span> <span className="font-semibold text-emerald-600">{row.con_charge}</span></div>
                     <div><span className="text-slate-400">Late:</span> {row.waiveOff
                       ? <Input value={row.lateFeeInput} onChange={e => { e.stopPropagation(); updateLateFee('current', row.id, e.target.value) }} type="number" className="w-20 inline-flex ml-1" />
                       : <span className="font-semibold text-amber-600">{row.latefee}</span>}</div>
                  </div>
                )}
              </div>
            ))}
          </div>
          {/* Mobile Next button */}
          {step === 1 && (
            <div className="sm:hidden p-3 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
              <Btn variant="primary" className="w-full" onClick={() => setStep(2)} icon={ArrowRight}>
                Next: Payment Details
                ({currDues.filter(r => r.selected).length} selected · ₹{(currTotal + prevTotal).toLocaleString('en-IN')})
              </Btn>
            </div>
          )}
        </SectionCard>
      )}

      {/* ── PAYMENT DETAILS ── */}
      <div className={step === 2 ? '' : 'hidden sm:block'}>
        <SectionCard>
          <SectionHeader icon={Banknote} title="Payment Details" color="emerald" />
          <div className="p-4 space-y-4">
            {/* Summary row */}
            <div className="grid grid-cols-2 sm:grid-cols-5 gap-3">
              <div>
                <p className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 mb-1">Payment Date <span className="text-rose-500">*</span></p>
                <Input type="date" value={payDate} onChange={e => setPayDate(e.target.value)} />
              </div>
              <div>
                <p className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 mb-1">Prev Year Dues</p>
                <Input value={prevTotal} disabled className="font-bold text-rose-600" />
              </div>
              <div>
                <p className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 mb-1">Current Dues</p>
                <Input value={currTotal} disabled className="font-bold text-blue-600" />
              </div>
              <div>
                <p className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 mb-1">Total Payable</p>
                <Input value={totalPayable} disabled className="font-bold text-slate-700 dark:text-slate-200" />
              </div>
              <div>
                <p className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 mb-1">Net Payable</p>
                <div className="px-3 py-2 rounded-lg bg-emerald-50 dark:bg-emerald-500/10 border-2 border-emerald-300 dark:border-emerald-500/30 text-center">
                  <span className="text-[16px] font-black text-emerald-700 dark:text-emerald-300 tabular-nums">₹{netPayable.toLocaleString('en-IN')}</span>
                </div>
              </div>
            </div>

            {/* Advance adjust (visible if advance > 0) */}
            {advance > 0 && (
              <div className="flex items-center gap-3 p-3 rounded-xl bg-violet-50 dark:bg-violet-500/8 border border-violet-200 dark:border-violet-500/20">
                <Shield className="w-4 h-4 text-violet-600 dark:text-violet-400 flex-shrink-0" />
                <span className="text-[12px] font-semibold text-violet-700 dark:text-violet-300">Advance Available: ₹{advance.toLocaleString('en-IN')}</span>
                <div className="flex-1 max-w-[120px]">
                  <Input value={adjAmt} onChange={e => setAdjAmt(Math.min(advance, parseFloat(e.target.value) || 0))} type="number" placeholder="Adjust amt" />
                </div>
                <Btn variant="ghost" size="sm">Adjust</Btn>
              </div>
            )}

            {/* MOP Grid */}
            <div>
              <p className="text-[12px] font-bold text-slate-600 dark:text-slate-300 mb-2 flex items-center gap-1.5">
                <CreditCard className="w-3.5 h-3.5 text-blue-500" /> Mode of Payment
              </p>
              <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] overflow-hidden">
                {/* Desktop headers */}
                <div className="hidden sm:grid grid-cols-[160px_1fr_1fr_1fr_120px_120px_auto_auto] gap-0 bg-slate-50 dark:bg-white/[0.02] border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
                  {['MOP', 'Cheque/Txn No.', 'Cheque Date', 'Bank', 'Amount', 'Use Amount', '', ''].map((h, i) => (
                    <div key={i} className="px-3 py-2 text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 border-r last:border-r-0 border-slate-100 dark:border-[rgba(99,102,241,0.08)]">{h}</div>
                  ))}
                </div>

                {mopRows.map((row, idx) => (
                  <div key={row.id} className={`border-b last:border-b-0 border-slate-100 dark:border-[rgba(99,102,241,0.08)]
                    ${idx % 2 === 0 ? '' : 'bg-slate-50/40 dark:bg-white/[0.01]'}`}>
                    {/* Desktop row */}
                    <div className="hidden sm:grid grid-cols-[160px_1fr_1fr_1fr_120px_120px_auto_auto] gap-0 items-center">
                      <div className="px-2 py-2 border-r border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
                        <NativeSelect value={row.mop} onChange={e => updateMop(row.id, 'mop', e.target.value)}>
                          {(bootstrap?.mops || []).map(m => <option key={m.id || m.name} value={m.name || m.label}>{m.name || m.label}</option>)}
                        </NativeSelect>
                      </div>
                      <div className="px-2 py-2 border-r border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
                         <Input value={row.chqNo} disabled={row.mop === 'Cash' || row.mop === 'Select'} onChange={e => updateMop(row.id, 'chqNo', e.target.value)} placeholder="Cheque/Txn no." />
                      </div>
                      <div className="px-2 py-2 border-r border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
                         <Input type="date" value={row.chqDate} disabled={row.mop === 'Cash' || row.mop === 'Select'} onChange={e => updateMop(row.id, 'chqDate', e.target.value)} />
                      </div>
                      <div className="px-2 py-2 border-r border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
                         <NativeSelect value={row.bank} disabled={row.mop === 'Cash' || row.mop === 'Select'} onChange={e => updateMop(row.id, 'bank', e.target.value)} placeholder="Select Bank">
                          {(bootstrap?.banks || []).map(b => <option key={b.id || b.name} value={b.id}>{b.name || b.label}</option>)}
                        </NativeSelect>
                      </div>
                      <div className="px-2 py-2 border-r border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
                        <Input value={row.amount} onChange={e => updateMop(row.id, 'amount', e.target.value)} placeholder="0" type="number" />
                      </div>
                      <div className="px-2 py-2 border-r border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
                        <Input value={row.useAmount} onChange={e => updateMop(row.id, 'useAmount', e.target.value)} placeholder="0" type="number" />
                      </div>
                      <div className="px-2 py-2 border-r border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
                        <Btn variant="success" size="xs" onClick={addMopRow} icon={Plus}>ADD</Btn>
                      </div>
                      <div className="px-2 py-2">
                        <Btn variant="danger" size="xs" onClick={() => mopRows.length > 1 && removeMopRow(row.id)} icon={Minus} disabled={mopRows.length === 1}>Rem</Btn>
                      </div>
                    </div>

                    {/* Mobile row */}
                    <div className="sm:hidden p-3 space-y-2.5">
                      <div className="grid grid-cols-2 gap-2">
                        <div>
                          <p className="text-[10px] font-semibold text-slate-400 mb-1">MOP</p>
                          <NativeSelect value={row.mop} onChange={e => updateMop(row.id, 'mop', e.target.value)}>
                          {(bootstrap?.mops || []).map(m => <option key={m.id || m.name} value={m.name || m.label}>{m.name || m.label}</option>)}
                          </NativeSelect>
                        </div>
                        <div>
                          <p className="text-[10px] font-semibold text-slate-400 mb-1">Amount</p>
                          <Input value={row.amount} onChange={e => updateMop(row.id, 'amount', e.target.value)} placeholder="0" type="number" />
                        </div>
                      </div>
                      {row.mop !== 'Cash' && (
                        <div className="grid grid-cols-2 gap-2">
                          <div>
                            <p className="text-[10px] font-semibold text-slate-400 mb-1">Cheque/Txn No.</p>
                           <Input value={row.chqNo} disabled={row.mop === 'Cash' || row.mop === 'Select'} onChange={e => updateMop(row.id, 'chqNo', e.target.value)} placeholder="No." />
                          </div>
                          <div>
                            <p className="text-[10px] font-semibold text-slate-400 mb-1">Bank</p>
                             <NativeSelect value={row.bank} disabled={row.mop === 'Cash' || row.mop === 'Select'} onChange={e => updateMop(row.id, 'bank', e.target.value)} placeholder="Select">
                              {(bootstrap?.banks || []).map(b => <option key={b.id || b.name} value={b.id}>{b.name || b.label}</option>)}
                            </NativeSelect>
                          </div>
                         </div>
                       )}
                       {row.mop === 'Online' && (
                         <div>
                           <p className="text-[10px] font-semibold text-slate-400 mb-1">Settlement Date</p>
                           <Input type="date" value={row.settlementDate || ''} onChange={e => updateMop(row.id, 'settlementDate', e.target.value)} />
                         </div>
                       )}
                      <div className="flex gap-2 justify-end">
                        <Btn variant="success" size="xs" onClick={addMopRow} icon={Plus}>Add Row</Btn>
                        {mopRows.length > 1 && <Btn variant="danger" size="xs" onClick={() => removeMopRow(row.id)} icon={Minus}>Remove</Btn>}
                      </div>
                    </div>
                  </div>
                ))}

                {/* MOP Total */}
                <div className="flex items-center justify-between px-4 py-3 bg-slate-50 dark:bg-white/[0.02] border-t border-slate-200 dark:border-[rgba(99,102,241,0.1)]">
                  <span className="text-[12px] font-bold text-slate-500 dark:text-slate-400">Total MOP Amount</span>
                  <span className={`text-[15px] font-black tabular-nums ${mopTotal >= netPayable ? 'text-emerald-600' : 'text-rose-600'}`}>
                    ₹{mopTotal.toLocaleString('en-IN')}
                    {mopTotal >= netPayable && netPayable > 0 && <Check className="w-4 h-4 inline ml-1.5" />}
                  </span>
                </div>
              </div>
            </div>

            {/* Action buttons */}
            <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-2 pt-2">
              <Btn variant="success" size="lg" onClick={handleDeposit} disabled={deposited} className="flex-1 sm:flex-none sm:w-40" icon={Check}>
                {deposited ? 'Deposited!' : 'Deposit'}
              </Btn>
            </div>
          </div>
        </SectionCard>
      </div>

      {/* Mobile: back to step 1 */}
      {step === 2 && (
        <button
          className="flex sm:hidden items-center gap-1.5 text-[13px] font-semibold text-blue-600 dark:text-blue-400"
          onClick={() => setStep(1)}
        >
          <ChevronLeft className="w-4 h-4" /> Back to Select Dues
        </button>
      )}
    </div>
  )
}

// ─── TAB 2: FEE SUMMARY ──────────────────────────────────────────────────────

function FeeSummaryTab({ rows = [] }) {
  const [sessions, setSessions] = useState([])

  useEffect(() => {
    const grouped = rows.reduce((acc, row) => {
      const session = row.session || row.financialYear || row.sessionName || 'Current'
      const item = {
        installment_no: row.installmentNo ?? row.installment_no ?? row.instNo ?? '',
        headName: row.headName || row.feeHead || row.fee_head || row.feeHeadName || 'Fee',
        head_amount: Number(row.headAmount ?? row.head_amount ?? row.amount ?? 0),
        concession: Number(row.concession ?? 0),
        receive_amount: Number(row.receiveAmount ?? row.receive_amount ?? row.received ?? 0),
        balance: Number(row.balance ?? row.dueAmount ?? 0),
      }
      if (!acc[session]) acc[session] = []
      acc[session].push(item)
      return acc
    }, {})
    setSessions(Object.entries(grouped).map(([session, heads]) => ({ session, heads, expanded: true })))
  }, [rows])

  const toggle = (i) => setSessions(s => s.map((x, idx) => idx === i ? { ...x, expanded: !x.expanded } : x))

  return (
    <div className="space-y-3">
      {sessions.map((sess, si) => {
        const total = sess.heads.reduce((s, h) => s + h.head_amount, 0)
        const paid  = sess.heads.reduce((s, h) => s + h.receive_amount, 0)
        const bal   = sess.heads.reduce((s, h) => s + h.balance, 0)
        return (
          <SectionCard key={si}>
            <button
              type="button"
              onClick={() => toggle(si)}
              className="w-full flex items-center justify-between gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
            >
              <div className="flex items-center gap-3 min-w-0">
                <span className="w-8 h-8 rounded-xl bg-blue-100 dark:bg-blue-500/15 flex items-center justify-center flex-shrink-0">
                  <BookOpen className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                </span>
                <div className="min-w-0">
                  <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Session {sess.session}</p>
                  <p className="text-[11px] text-slate-400 dark:text-slate-500">{sess.heads.length} fee records</p>
                </div>
              </div>
              <div className="flex items-center gap-4 flex-shrink-0">
                <div className="hidden sm:flex gap-4">
                  <Pill label="Total"  value={total} color="blue"  />
                  <Pill label="Paid"   value={paid}  color="green" />
                  <Pill label="Balance" value={bal}  color={bal > 0 ? 'red' : 'slate'} />
                </div>
                {sess.expanded ? <ChevronUp className="w-4 h-4 text-slate-400" /> : <ChevronDown className="w-4 h-4 text-slate-400" />}
              </div>
            </button>

            {/* Mobile mini pills */}
            {!sess.expanded && (
              <div className="flex sm:hidden gap-3 px-4 pb-3">
                <Pill label="Total"  value={total} color="blue"  />
                <Pill label="Paid"   value={paid}  color="green" />
                <Pill label="Bal"    value={bal}   color={bal > 0 ? 'red' : 'slate'} />
              </div>
            )}

            {sess.expanded && (
              <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
                {/* Desktop table */}
                <div className="hidden sm:block overflow-x-auto">
                  <table className="w-full">
                    <thead className="bg-slate-50/50 dark:bg-white/[0.02]">
                      <tr>
                        {['Inst No.', 'Head Name', 'Head Amt', 'Concession', 'Rec. Amt', 'Balance'].map((h, i) => (
                          <th key={i} className="px-4 py-2.5 text-[11px] font-bold uppercase tracking-wide text-left text-slate-500 dark:text-slate-400">{h}</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {sess.heads.map((h, hi) => (
                        <tr key={hi} className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.06)] hover:bg-slate-50/50 dark:hover:bg-white/[0.01]">
                          <td className="px-4 py-2.5"><Badge color="blue">{h.installment_no}</Badge></td>
                          <td className="px-4 py-2.5 text-[13px] font-medium text-slate-700 dark:text-slate-200">{h.headName}</td>
                          <td className="px-4 py-2.5"><AmtDisplay value={h.head_amount} /></td>
                          <td className="px-4 py-2.5"><AmtDisplay value={h.concession} color="green" /></td>
                          <td className="px-4 py-2.5"><AmtDisplay value={h.receive_amount} color="green" /></td>
                          <td className="px-4 py-2.5"><AmtDisplay value={h.balance} color={h.balance > 0 ? 'red' : 'green'} /></td>
                        </tr>
                      ))}
                      {/* Totals row */}
                      <tr className="border-t-2 border-blue-200 dark:border-blue-500/20 bg-blue-50/30 dark:bg-blue-500/5">
                        <td colSpan={2} className="px-4 py-2.5 text-[12px] font-bold text-blue-700 dark:text-blue-300">Grand Total</td>
                        <td className="px-4 py-2.5 font-bold text-[13px] text-blue-700 dark:text-blue-300 tabular-nums">{total.toLocaleString('en-IN')}</td>
                        <td></td>
                        <td className="px-4 py-2.5 font-bold text-[13px] text-emerald-600 dark:text-emerald-400 tabular-nums">{paid.toLocaleString('en-IN')}</td>
                        <td className="px-4 py-2.5 font-bold text-[13px] tabular-nums text-rose-600 dark:text-rose-400">{bal.toLocaleString('en-IN')}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                {/* Mobile cards */}
                <div className="sm:hidden p-3 space-y-2">
                  {sess.heads.map((h, hi) => (
                    <div key={hi} className="rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1e2238] p-3">
                      <div className="flex items-center justify-between mb-2">
                        <div className="flex items-center gap-2">
                          <Badge color="blue">{h.installment_no}</Badge>
                          <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200">{h.headName}</span>
                        </div>
                        <Badge color={h.balance > 0 ? 'red' : 'green'} size="xs">{h.balance > 0 ? 'Due' : 'Paid'}</Badge>
                      </div>
                      <div className="grid grid-cols-3 gap-1 text-[11px]">
                        <div><span className="text-slate-400">Amount:</span> <span className="font-semibold">{h.head_amount}</span></div>
                        <div><span className="text-slate-400">Paid:</span> <span className="font-semibold text-emerald-600">{h.receive_amount}</span></div>
                        <div><span className="text-slate-400">Balance:</span> <span className={`font-bold ${h.balance > 0 ? 'text-rose-600' : 'text-emerald-600'}`}>{h.balance}</span></div>
                      </div>
                    </div>
                  ))}
                  {/* Mobile total */}
                  <div className="flex gap-3 justify-end pt-1">
                    <Pill label="Paid" value={paid} color="green" />
                    <Pill label="Balance" value={bal} color={bal > 0 ? 'red' : 'slate'} />
                  </div>
                </div>
              </div>
            )}
          </SectionCard>
        )
      })}
    </div>
  )
}

// ─── TAB 3: PREVIOUS TXN ─────────────────────────────────────────────────────

function PreviousTxnTab({ showToast, rows = [], studentId, session }) {
  const [sessions, setSessions] = useState([])

  useEffect(() => {
    const grouped = rows.reduce((acc, row) => {
      const session = row.session || row.financialYear || row.sessionName || 'Current'
      const item = {
        receipt_no: row.receiptNo || row.receipt_no || row.receipt || '',
        paymentdate: row.paymentDate || row.paymentdate || row.payDate || '',
        inst_no: row.installmentNo ?? row.installment_no ?? row.instNo ?? '',
        mop: row.mop || row.payMode || row.pay_mode || '',
        transaction_no: row.transactionNo || row.transaction_no || '',
        transaction_date: row.transactionDate || row.transaction_date || '',
        installment_amount: Number(row.installmentAmount ?? row.instAmount ?? row.amount ?? 0),
        latefee: Number(row.lateFee ?? row.late_fee ?? 0),
        advance_amount: Number(row.extraAmount ?? row.extra_amount ?? row.advanceAmount ?? 0),
        amount: Number(row.amount ?? row.transactionAmount ?? 0),
        status: String(row.status || 'active').toLowerCase(),
      }
      if (!acc[session]) acc[session] = []
      acc[session].push(item)
      return acc
    }, {})
    setSessions(Object.entries(grouped).map(([session, txns]) => ({ session, txns, expanded: true })))
  }, [rows])

  const toggle = (i) => setSessions(s => s.map((x, idx) => idx === i ? { ...x, expanded: !x.expanded } : x))

  const handlePrint = async (txn) => {
    try {
      const data = await apiFetch(`/fees/transactions/${studentId}/receipt?receiptNo=${encodeURIComponent(txn.receipt_no)}`)
      const header = data.header || {}, student = data.student?.[0] || {}, lines = data.lines || []
      const w = window.open('', '_blank', 'width=850,height=700')
      if (!w) { showToast('Allow popups to print the receipt.', 'error'); return }
      w.document.write(`<html><head><title>Receipt ${txn.receipt_no}</title><style>body{font-family:Arial;padding:28px}h2{text-align:center}table{width:100%;border-collapse:collapse;margin-top:18px}td,th{border:1px solid #bbb;padding:8px;text-align:left}.right{text-align:right}</style></head><body><h2>Fee Receipt</h2><p><b>Student:</b> ${student.name || ''}<br/><b>Admission No:</b> ${student.admNo || header.admno || ''}<br/><b>Receipt No:</b> ${header.receiptNo || txn.receipt_no}<br/><b>Payment Date:</b> ${header.paymentDate || txn.paymentdate || ''}</p><table><thead><tr><th>Installment</th><th>Fee Head</th><th class="right">Amount</th></tr></thead><tbody>${lines.map(line => `<tr><td>${line.installmentNo || ''}</td><td>${line.feeHead || ''}</td><td class="right">${line.amount || 0}</td></tr>`).join('')}</tbody></table><h3 class="right">Total: ${header.amount || txn.amount || 0}</h3></body></html>`)
      w.document.close(); w.focus(); w.print()
    } catch (error) { showToast(error.message || 'Receipt could not be opened.', 'error') }
  }
  const handleDelete = async (txn) => {
    if (!window.confirm(`Delete receipt ${txn.receipt_no}?`)) return
    const remark = window.prompt('Delete remark:', '') ?? ''
    try { await apiFetch('/fees/transactions/delete', { method: 'POST', body: JSON.stringify({ receiptNo: txn.receipt_no, session, remark }) }); setSessions(s => s.map(group => ({ ...group, txns: group.txns.filter(item => item.receipt_no !== txn.receipt_no) }))); showToast('Fee deleted successfully.') } catch (error) { showToast(error.message || 'Fee delete failed.', 'error') }
  }
  const handleEdit = async (txn) => {
    const mop = window.prompt('Payment mode:', txn.mop || 'Cash'); if (mop === null) return
    const transactionNo = window.prompt('Transaction/Cheque no.:', txn.transaction_no || ''); if (transactionNo === null) return
    const transactionDate = window.prompt('Transaction date (YYYY-MM-DD):', txn.transaction_date || ''); if (transactionDate === null) return
    try { await apiFetch('/fees/transactions', { method: 'PUT', body: JSON.stringify({ receiptNo: txn.receipt_no, session, mop, transactionNo, transactionDate, paymentDate: txn.paymentdate }) }); setSessions(s => s.map(group => ({ ...group, txns: group.txns.map(item => item.receipt_no === txn.receipt_no ? { ...item, mop, transaction_no: transactionNo, transaction_date: transactionDate } : item) }))); showToast('Student fee data updated successfully.') } catch (error) { showToast(error.message || 'Fee update failed.', 'error') }
  }

  return (
    <div className="space-y-3">
      {sessions.map((sess, si) => {
        const total = sess.txns.reduce((s, t) => s + t.amount, 0)
        return (
          <SectionCard key={si}>
            <button
              type="button"
              onClick={() => toggle(si)}
              className="w-full flex items-center justify-between gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
            >
              <div className="flex items-center gap-3 min-w-0">
                <span className="w-8 h-8 rounded-xl bg-violet-100 dark:bg-violet-500/15 flex items-center justify-center flex-shrink-0">
                  <Hash className="w-4 h-4 text-violet-600 dark:text-violet-400" />
                </span>
                <div>
                  <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Session {sess.session}</p>
                  <p className="text-[11px] text-slate-400 dark:text-slate-500">{sess.txns.length} transactions</p>
                </div>
              </div>
              <div className="flex items-center gap-3 flex-shrink-0">
                <Pill label="Total Paid" value={total} color="green" />
                {sess.expanded ? <ChevronUp className="w-4 h-4 text-slate-400" /> : <ChevronDown className="w-4 h-4 text-slate-400" />}
              </div>
            </button>

            {sess.expanded && (
              <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
                {/* Desktop table */}
                <div className="hidden sm:block overflow-x-auto">
                  <table className="w-full">
                    <thead className="bg-slate-50/50 dark:bg-white/[0.02]">
                      <tr>
                        {['Rec No.', 'Rec Date', 'Inst.', 'MOP', 'Txn No.', 'Txn Date', 'Inst Amt', 'Late Fee', 'Extra Amt', 'Total', 'Status', 'Actions'].map((h, i) => (
                          <th key={i} className="px-3 py-2.5 text-[11px] font-bold uppercase tracking-wide text-left text-slate-500 dark:text-slate-400 whitespace-nowrap">{h}</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {sess.txns.map((t, ti) => (
                        <tr key={ti} className={`border-t border-slate-100 dark:border-[rgba(99,102,241,0.06)] hover:bg-slate-50/50 dark:hover:bg-white/[0.01] ${t.status === 'inactive' ? 'opacity-60' : ''}`}>
                          <td className="px-3 py-2.5">
                            <span className={`text-[12px] font-semibold ${t.status === 'inactive' ? 'text-rose-500 line-through' : 'text-blue-600 dark:text-blue-400'}`}>{t.receipt_no}</span>
                          </td>
                          <td className="px-3 py-2.5 text-[12px] text-slate-600 dark:text-slate-300 whitespace-nowrap">{t.paymentdate}</td>
                          <td className="px-3 py-2.5"><Badge color="blue" size="xs">{t.inst_no}</Badge></td>
                          <td className="px-3 py-2.5">
                            <span className="flex items-center gap-1 text-[12px] text-slate-600 dark:text-slate-300">
                              {mopIcon(t.mop)} {t.mop}
                            </span>
                          </td>
                          <td className="px-3 py-2.5 text-[12px] text-slate-400">{t.transaction_no || '—'}</td>
                          <td className="px-3 py-2.5 text-[12px] text-slate-400">{t.transaction_date || '—'}</td>
                          <td className="px-3 py-2.5"><AmtDisplay value={t.installment_amount} /></td>
                          <td className="px-3 py-2.5"><AmtDisplay value={t.latefee} color="amber" /></td>
                          <td className="px-3 py-2.5"><AmtDisplay value={t.advance_amount} /></td>
                          <td className="px-3 py-2.5"><span className="text-[13px] font-bold text-slate-800 dark:text-slate-100 tabular-nums">{t.amount.toLocaleString('en-IN')}</span></td>
                          <td className="px-3 py-2.5">
                            <Badge color={t.status === 'active' ? 'green' : 'red'} size="xs">{t.status}</Badge>
                          </td>
                          <td className="px-3 py-2.5">
                            <div className="flex items-center gap-1">
                              <Btn variant="ghost" size="xs" onClick={() => handlePrint(t)} icon={Printer}>Print</Btn>
                              <Btn variant="ghost" size="xs" onClick={() => handleEdit(t)} icon={Pencil}>Edit</Btn>
                              <Btn variant="ghost" size="xs" onClick={() => handleDelete(t)} icon={Trash2} className="text-rose-600 hover:bg-rose-50">Del</Btn>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                {/* Mobile cards */}
                <div className="sm:hidden p-3 space-y-3">
                  {sess.txns.map((t, ti) => (
                    <div key={ti} className={`rounded-xl border-2 p-3 ${t.status === 'inactive' ? 'border-rose-200 dark:border-rose-500/20 opacity-75' : 'border-slate-100 dark:border-[rgba(99,102,241,0.2)]'} bg-white dark:bg-[#1e2238]`}>
                      <div className="flex items-start justify-between gap-2 mb-2">
                        <div>
                          <p className={`text-[13px] font-bold ${t.status === 'inactive' ? 'text-rose-500 line-through' : 'text-blue-600 dark:text-blue-400'}`}>{t.receipt_no}</p>
                          <p className="text-[11px] text-slate-400 mt-0.5">{t.paymentdate}</p>
                        </div>
                        <div className="text-right">
                          <p className="text-[16px] font-black text-slate-800 dark:text-slate-100 tabular-nums">₹{t.amount.toLocaleString('en-IN')}</p>
                          <Badge color={t.status === 'active' ? 'green' : 'red'} size="xs">{t.status}</Badge>
                        </div>
                      </div>
                      <div className="grid grid-cols-2 gap-1 text-[11px] mb-3">
                        <div><span className="text-slate-400">Inst:</span> <span className="font-semibold text-slate-600 dark:text-slate-300">{t.inst_no}</span></div>
                        <div className="flex items-center gap-1"><span className="text-slate-400">MOP:</span> <span className="font-semibold text-slate-600 dark:text-slate-300 flex items-center gap-0.5">{mopIcon(t.mop)} {t.mop}</span></div>
                        {t.latefee > 0 && <div><span className="text-slate-400">Late Fee:</span> <span className="font-semibold text-amber-600">{t.latefee}</span></div>}
                        {t.advance_amount > 0 && <div><span className="text-slate-400">Advance:</span> <span className="font-semibold text-emerald-600">{t.advance_amount}</span></div>}
                      </div>
                      <div className="flex gap-1.5">
                        <Btn variant="ghost" size="xs" onClick={() => handlePrint(t)} icon={Printer} className="flex-1">Print</Btn>
                        <Btn variant="ghost" size="xs" onClick={() => handleEdit(t)} icon={Pencil} className="flex-1">Edit</Btn>
                        <Btn variant="ghost" size="xs" onClick={() => handleDelete(t)} icon={Trash2} className="flex-1 text-rose-600 hover:bg-rose-50">Delete</Btn>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </SectionCard>
        )
      })}
    </div>
  )
}

// ─── TAB 4: DEPOSIT MISC FEE ─────────────────────────────────────────────────

function DepositMiscFeeTab({ showToast, student, session, bootstrap }) {
  const [liveMiscHeads, setLiveMiscHeads] = useState([])
  const liveMops = bootstrap?.mops || []
  const liveBanks = bootstrap?.banks || []
  useEffect(() => { setLiveMiscHeads((bootstrap?.miscHeads || []).map(h => ({ ...h, name: h.name || h.label }))) }, [bootstrap])
  useEffect(() => { setMiscRows(rows => rows.map(row => ({ ...row, session }))) }, [session])
  const [miscRows, setMiscRows] = useState([
    { id: 1, session: '', head: '', miscFee: '', lateFee: '', waiveOff: false, remark: '' }
  ])
  const [chqRows, setChqRows] = useState([
    { id: 1, amount: '', mop: 'Cash', chqNo: '', chqDate: '', bank: '' }
  ])

  const updateMisc = (id, key, val) => setMiscRows(r => r.map(x => x.id === id ? { ...x, [key]: val } : x))
  const updateChq  = (id, key, val) => setChqRows(r => r.map(x => x.id === id ? { ...x, [key]: val } : x))

  const totalMisc = miscRows.reduce((s, r) => s + (parseFloat(r.miscFee) || 0) + (parseFloat(r.lateFee) || 0), 0)
  const submitMisc = async (withReceipt) => {
    const row = miscRows[0], payment = chqRows[0]
    if (!student?.admNo || !row.head) { showToast('Student and miscellaneous head are required.', 'error'); return }
    try {
      const result = await apiFetch('/fees/work/miscellaneous', { method: 'POST', body: JSON.stringify({ admissionNo: student.admNo, session, headId: row.head, amount: Number(row.miscFee || 0), lateFee: Number(row.lateFee || 0), waiveOff: row.waiveOff, paymentDate: new Date().toISOString().slice(0, 10), remark: row.remark, mop: payment.mop, bankId: payment.bank, transactionNo: payment.chqNo, transactionDate: payment.chqDate }) })
      showToast(`Miscellaneous fee deposited. Receipt ${result.receiptNo || ''}`)
      if (withReceipt) {
        const receipt = await apiFetch(`/fees/work/miscellaneous/${student.id}/receipt?receiptNo=${encodeURIComponent(result.receiptNo)}`)
        const header = receipt.header || {}, person = receipt.student?.[0] || {}, lines = receipt.lines || []
        const w = window.open('', '_blank', 'width=850,height=700')
        if (!w) { showToast('Allow popups to print the receipt.', 'error'); return }
        w.document.write(`<html><head><title>Receipt ${header.receiptNo || result.receiptNo}</title><style>body{font-family:Arial;padding:28px}h2{text-align:center}table{width:100%;border-collapse:collapse;margin-top:18px}td,th{border:1px solid #bbb;padding:8px;text-align:left}.right{text-align:right}</style></head><body><h2>Miscellaneous Fee Receipt</h2><p><b>Student:</b> ${person.name || ''}<br/><b>Admission No:</b> ${person.admNo || header.admno || ''}<br/><b>Receipt No:</b> ${header.receiptNo || result.receiptNo}<br/><b>Payment Date:</b> ${header.paymentDate || ''}</p><table><thead><tr><th>Fee Head</th><th>Remark</th><th class="right">Amount</th></tr></thead><tbody>${lines.map(line => `<tr><td>${line.feeHead || ''}</td><td>${line.remark || ''}</td><td class="right">${line.amount || 0}</td></tr>`).join('')}</tbody></table><h3 class="right">Total: ${header.amount || result.amount || 0}</h3></body></html>`)
        w.document.close(); w.focus(); w.print()
      }
    } catch (error) { showToast(error.message || 'Miscellaneous fee deposit failed.', 'error') }
  }

  return (
    <div className="space-y-4">
      <SectionCard>
        <SectionHeader icon={Settings} title="Miscellaneous Fee Heads" color="violet" />
        {/* Desktop */}
        <div className="hidden sm:block overflow-x-auto">
          <table className="w-full">
            <thead className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/40 dark:bg-white/[0.02]">
              <tr>
                {['Misc. Fee Head', 'Misc. Fee', 'Late Fee', 'Waive Off', 'Remark'].map((h, i) => (
                  <th key={i} className="px-3 py-2.5 text-[11px] font-bold uppercase tracking-wide text-left text-slate-500 dark:text-slate-400">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {miscRows.map(row => (
                <tr key={row.id} className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.06)]">
                  <td className="px-3 py-2.5">
                    <NativeSelect value={row.head} onChange={e => updateMisc(row.id, 'head', e.target.value)} placeholder="Select Misc Head">
                      {liveMiscHeads.map(h => <option key={h.id} value={h.id}>{h.name}</option>)}
                    </NativeSelect>
                  </td>
                  <td className="px-3 py-2.5"><Input value={row.miscFee} onChange={e => updateMisc(row.id, 'miscFee', e.target.value)} type="number" placeholder="0" className="w-28" /></td>
                  <td className="px-3 py-2.5"><Input value={row.lateFee} onChange={e => updateMisc(row.id, 'lateFee', e.target.value)} type="number" placeholder="0" className="w-28" /></td>
                  <td className="px-3 py-2.5"><Checkbox checked={row.waiveOff} onChange={() => updateMisc(row.id, 'waiveOff', !row.waiveOff)} label="Waive Off" /></td>
                  <td className="px-3 py-2.5"><Input value={row.remark} onChange={e => updateMisc(row.id, 'remark', e.target.value)} placeholder="Remark..." /></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {/* Mobile */}
        <div className="sm:hidden p-3 space-y-3">
          {miscRows.map(row => (
            <div key={row.id} className="rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.2)] p-3 space-y-2.5 bg-white dark:bg-[#1e2238]">
              <div>
                <p className="text-[10px] font-semibold text-slate-400 mb-1">Misc. Fee Head</p>
                <NativeSelect value={row.head} onChange={e => updateMisc(row.id, 'head', e.target.value)} placeholder="Select Head">
                  {liveMiscHeads.map(h => <option key={h.id} value={h.id}>{h.name}</option>)}
                </NativeSelect>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <p className="text-[10px] font-semibold text-slate-400 mb-1">Misc Fee</p>
                  <Input value={row.miscFee} onChange={e => updateMisc(row.id, 'miscFee', e.target.value)} type="number" placeholder="0" />
                </div>
                <div>
                  <p className="text-[10px] font-semibold text-slate-400 mb-1">Late Fee</p>
                  <Input value={row.lateFee} onChange={e => updateMisc(row.id, 'lateFee', e.target.value)} type="number" placeholder="0" />
                </div>
              </div>
              <div className="flex items-center justify-between">
                <Checkbox checked={row.waiveOff} onChange={() => updateMisc(row.id, 'waiveOff', !row.waiveOff)} label="Waive Off Late Fee" />
                <Input value={row.remark} onChange={e => updateMisc(row.id, 'remark', e.target.value)} placeholder="Remark" className="w-36 text-[11px]" />
              </div>
            </div>
          ))}
        </div>
      </SectionCard>

      <SectionCard>
        <SectionHeader icon={CreditCard} title="Payment Mode" color="emerald"
          actions={
            <div className="flex items-center gap-2">
              <span className="text-[12px] font-semibold text-slate-500 dark:text-slate-400">Total:</span>
              <span className="text-[14px] font-black text-emerald-600 dark:text-emerald-400 tabular-nums">₹{totalMisc.toLocaleString('en-IN')}</span>
            </div>
          }
        />
        <div className="p-3 space-y-2">
          {chqRows.map(row => (
            <div key={row.id} className="grid grid-cols-1 sm:grid-cols-6 gap-2 p-3 rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1e2238]">
              <div>
                <p className="text-[10px] font-semibold text-slate-400 mb-1">Amount</p>
                <Input value={row.amount} onChange={e => updateChq(row.id, 'amount', e.target.value)} type="number" placeholder="0" />
              </div>
              <div>
                <p className="text-[10px] font-semibold text-slate-400 mb-1">MOP</p>
                <NativeSelect value={row.mop} onChange={e => updateChq(row.id, 'mop', e.target.value)}>
                  {liveMops.map(m => <option key={m.id || m.name} value={m.name || m.label}>{m.name || m.label}</option>)}
                </NativeSelect>
              </div>
              <div>
                <p className="text-[10px] font-semibold text-slate-400 mb-1">Cheque/Txn No.</p>
                <Input value={row.chqNo} onChange={e => updateChq(row.id, 'chqNo', e.target.value)} placeholder="No." />
              </div>
              <div>
                <p className="text-[10px] font-semibold text-slate-400 mb-1">Txn Date</p>
                <Input type="date" value={row.chqDate} onChange={e => updateChq(row.id, 'chqDate', e.target.value)} />
              </div>
              <div>
                <p className="text-[10px] font-semibold text-slate-400 mb-1">Bank</p>
                <NativeSelect value={row.bank} onChange={e => updateChq(row.id, 'bank', e.target.value)} placeholder="Select Bank">
                  {liveBanks.map(b => <option key={b.id || b.name} value={b.id}>{b.name || b.label}</option>)}
                </NativeSelect>
              </div>
              <div className="flex items-end gap-1">
                <Btn variant="success" size="sm" icon={Plus} onClick={() => setChqRows(r => [...r, { id: Date.now(), amount: '', mop: 'Cash', chqNo: '', chqDate: '', bank: '' }])}>Add</Btn>
                {chqRows.length > 1 && <Btn variant="danger" size="sm" icon={Minus} onClick={() => setChqRows(r => r.filter(x => x.id !== row.id))}>Rem</Btn>}
              </div>
            </div>
          ))}
        </div>
        <div className="flex flex-col sm:flex-row gap-2 px-4 pb-4">
          <Btn variant="success" size="lg" className="flex-1 sm:flex-none sm:w-40" icon={Check} onClick={() => submitMisc(false)}>Deposit</Btn>
          <Btn variant="primary" size="lg" className="flex-1 sm:flex-none sm:w-52" icon={Printer} onClick={() => submitMisc(true)}>Deposit + Receipt</Btn>
        </div>
      </SectionCard>
    </div>
  )
}

// ─── TAB 5: ASSIGN OPTIONAL FEE ──────────────────────────────────────────────

function AssignOptionalFeeTab({ showToast, session, bootstrap, student }) {
  const [selectedHead, setSelectedHead] = useState('')
  const [students, setStudents] = useState([])
  const optionalHeads = (bootstrap?.feeHeads || []).filter(h => h.isOptional).map(h => ({ id: h.feeHeadId || h.id, name: h.name || h.feeHead }))
  useEffect(() => {
    if (!session || !selectedHead) { setStudents([]); return }
    apiFetch(`/fees/optional?session=${encodeURIComponent(session)}&feeHeadId=${encodeURIComponent(selectedHead)}&admissionNo=${encodeURIComponent(student?.admNo || '')}`)
      .then(rows => setStudents((rows || []).map(s => ({ ...s, id: s.studentId || s.id, admNo: s.admNo || s.registrationNo, name: s.name || s.studentName, sts: Boolean(s.sts), installments: (s.installments || []).map(i => i.value ?? i.label), selectedInsts: s.selectedInsts || [] }))))
      .catch(e => showToast(e.message, 'error'))
  }, [session, selectedHead, student?.admNo, showToast])
  const [expanded, setExpanded] = useState({})

  const toggleStu = (id) => setStudents(s => s.map(x => x.id === id ? { ...x, sts: !x.sts } : x))
  const toggleAll = () => {
    const allChecked = students.every(s => s.sts)
    setStudents(s => s.map(x => ({ ...x, sts: !allChecked })))
  }
  const toggleInst = (stuId, inst) => {
    setStudents(s => s.map(x => {
      if (x.id !== stuId) return x
      const insts = x.selectedInsts || []
      return { ...x, selectedInsts: insts.includes(inst) ? insts.filter(i => i !== inst) : [...insts, inst] }
    }))
  }

  const allChecked = students.every(s => s.sts)

  return (
    <div className="space-y-4">
      <SectionCard>
        <SectionHeader icon={Star} title="Assign Optional Fee" color="amber" />
        <div className="p-4">
          <div className="w-full sm:w-64">
            <p className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 mb-1">Fee Head <span className="text-rose-500">*</span></p>
            <NativeSelect value={selectedHead} onChange={e => setSelectedHead(e.target.value)} placeholder="-- Select Fee Head --">
              {optionalHeads.map(h => <option key={h.id} value={h.id}>{h.name}</option>)}
            </NativeSelect>
          </div>
        </div>
      </SectionCard>

      {selectedHead && (
        <SectionCard>
          <SectionHeader icon={User} title="Students" color="blue"
            actions={<Checkbox checked={allChecked} onChange={toggleAll} label="Select All" />}
          />
          {/* Desktop */}
          <div className="hidden sm:block overflow-x-auto">
            <table className="w-full">
              <thead className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/40 dark:bg-white/[0.02]">
                <tr>
                  <th className="px-3 py-2.5 text-[11px] font-bold uppercase tracking-wide text-left text-slate-500 dark:text-slate-400">
                    <Checkbox checked={allChecked} onChange={toggleAll} label="Select All" />
                  </th>
                  {['S.No.', 'Adm No.', 'Student', 'Installments'].map((h, i) => (
                    <th key={i} className="px-3 py-2.5 text-[11px] font-bold uppercase tracking-wide text-left text-slate-500 dark:text-slate-400">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {students.map((s, si) => (
                  <tr key={s.id} className={`border-b border-slate-100 dark:border-[rgba(99,102,241,0.06)] transition-colors ${s.sts ? 'bg-blue-50/30 dark:bg-blue-500/5' : 'hover:bg-slate-50/50 dark:hover:bg-white/[0.02]'}`}>
                    <td className="px-3 py-2.5"><Checkbox checked={s.sts} onChange={() => toggleStu(s.id)} /></td>
                    <td className="px-3 py-2.5 text-[12px] text-slate-400">{si + 1}</td>
                    <td className="px-3 py-2.5"><Badge color="blue" size="xs">{s.admNo}</Badge></td>
                    <td className="px-3 py-2.5 text-[13px] font-semibold text-slate-700 dark:text-slate-200">{s.name}</td>
                    <td className="px-3 py-2.5">
                      <div className="flex flex-wrap gap-1.5">
                        {s.installments.map(inst => (
                          <button
                            key={inst}
                            type="button"
                            onClick={() => toggleInst(s.id, inst)}
                            className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold transition-all
                              ${(s.selectedInsts || []).includes(inst)
                                ? 'bg-blue-600 text-white dark:bg-indigo-500'
                                : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700'}`}
                          >
                            {inst}
                          </button>
                        ))}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {/* Mobile */}
          <div className="sm:hidden p-3 space-y-2">
            {students.map(s => (
              <div key={s.id} className={`rounded-xl border-2 p-3 transition-all ${s.sts ? 'border-blue-400 bg-blue-50 dark:bg-blue-500/8' : 'border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1e2238]'}`}>
                <div className="flex items-center gap-3 mb-2">
                  <Checkbox checked={s.sts} onChange={() => toggleStu(s.id)} />
                  <div className="flex-1">
                    <p className="text-[13px] font-bold text-slate-800 dark:text-slate-100">{s.name}</p>
                    <p className="text-[11px] text-slate-400">{s.admNo}</p>
                  </div>
                </div>
                {s.sts && (
                  <div>
                    <p className="text-[10px] font-semibold text-slate-400 mb-1.5">Select Installments:</p>
                    <div className="flex flex-wrap gap-1.5">
                      {s.installments.map(inst => (
                        <button
                          key={inst}
                          type="button"
                          onClick={() => toggleInst(s.id, inst)}
                          className={`px-2.5 py-1 rounded-lg text-[11px] font-semibold transition-all
                            ${(s.selectedInsts || []).includes(inst)
                              ? 'bg-blue-600 text-white'
                              : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300'}`}
                        >
                          {inst}
                        </button>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>
          <div className="p-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
            <Btn variant="success" size="lg" icon={Check} className="w-full sm:w-auto sm:min-w-[160px]" onClick={async () => {
              try {
                const result = await apiFetch('/fees/optional', { method: 'POST', body: JSON.stringify({ session, feeHeadId: selectedHead, students: students.map(s => ({ studentId: s.id, classId: s.classId, installments: s.sts ? (s.selectedInsts || []) : [] })) }) })
                showToast(result.message || 'Optional fee assignment saved.')
              } catch (error) { showToast(error.message || 'Optional fee assignment failed.', 'error') }
            }}>Submit</Btn>
          </div>
        </SectionCard>
      )}
    </div>
  )
}

// ─── MAIN COMPONENT ──────────────────────────────────────────────────────────

function AdjustmentDetailsTab({ student, session, showToast }) {
  const [type, setType] = useState('FeeReceivable')
  const [data, setData] = useState({ opening: [], rows: [] })
  useEffect(() => {
    if (!student?.id || !session) return
    apiFetch(`/fees/students/${student.id}/receivable?session=${encodeURIComponent(session)}`).then(setData).catch(error => showToast(error.message || 'Adjustment details could not be loaded.', 'error'))
  }, [student?.id, session, showToast])
  const opening = data.opening?.[0] || {}
  return (
    <SectionCard>
      <SectionHeader icon={BookOpen} title="Adjustment Details" color="violet" />
      <div className="p-4 space-y-4">
        <div className="w-full sm:w-64"><p className="text-[11px] font-semibold text-slate-400 mb-1">Details Type</p><NativeSelect value={type} onChange={e => setType(e.target.value)}><option value="FeeReceivable">Fee Receivable</option></NativeSelect></div>
        {type === 'FeeReceivable' && <>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3"><Pill label="Date" value={opening.createdDate || '—'} color="slate" /><Pill label="Opening" value={opening.opening || 0} color="blue" /><Pill label="Adjust" value={opening.adjust || 0} color="green" /><Pill label="Balance" value={opening.balance || 0} color="amber" /></div>
          <div className="overflow-x-auto rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)]"><table className="w-full text-[12px]"><thead className="bg-slate-50 dark:bg-white/[0.02]"><tr><th className="px-3 py-2 text-left">S.No.</th><th className="px-3 py-2 text-left">Deposit Date</th><th className="px-3 py-2 text-left">Inst No.</th><th className="px-3 py-2 text-left">Receipt No.</th><th className="px-3 py-2 text-right">Receive Amount</th></tr></thead><tbody>{(data.rows || []).map((r, i) => <tr key={`${r.uid || i}`} className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]"><td className="px-3 py-2">{i + 1}</td><td className="px-3 py-2">{r.depositDate || ''}</td><td className="px-3 py-2">{r.installmentNo || ''}</td><td className="px-3 py-2">{r.receiptNo || ''}</td><td className="px-3 py-2 text-right">{r.receiveAmount || 0}</td></tr>)}</tbody></table></div>
        </>}
      </div>
    </SectionCard>
  )
}

const TABS = [
  { id: 0, label: 'Deposit Fee',      shortLabel: 'Deposit',  icon: CreditCard  },
  { id: 1, label: 'Fee Summary',      shortLabel: 'Summary',  icon: BarChart3   },
  { id: 2, label: 'Previous Txn',     shortLabel: 'Txn',      icon: Clock       },
  { id: 3, label: 'Deposit Misc. Fee', shortLabel: 'Misc',     icon: Settings    },
  { id: 4, label: 'Assign Optional Fee', shortLabel: 'Optional', icon: Star        },
  { id: 5, label: 'Adjustment Details', shortLabel: 'Adjust', icon: BookOpen    },
]

export default function DepositFee({ onPageClick }) {
  const [session,    setSession]    = useState('')
  const [feeType,    setFeeType]    = useState('')
  const [classId,    setClassId]    = useState('')
  const [admNo,      setAdmNo]      = useState('')
  const [feeBookNo,  setFeeBookNo]  = useState('')
  const [studentId,  setStudentId]  = useState('')
  const [student,    setStudent]    = useState(null)
  const [activeTab,  setActiveTab]  = useState(0)
  const [toast,      setToast]      = useState(null)
  const [loading,    setLoading]    = useState(false)
  const [mobileMenu, setMobileMenu] = useState(false)
  const [bootstrap, setBootstrap] = useState({ schoolId: '', sessions: [], classes: [], feeTypes: [] })
  const [studentOptions, setStudentOptions] = useState([])
  const [feeData, setFeeData] = useState(null)
  const [summaryRows, setSummaryRows] = useState([])
  const [transactionRows, setTransactionRows] = useState([])
  const [legacyPrefill, setLegacyPrefill] = useState(null)

  useEffect(() => {
    apiFetch('/fees/bootstrap').then(data => { setBootstrap(data); setSession(data.session || data.sessions?.[0]?.value || ''); setFeeType(String(data.feeTypes?.[0]?.id ?? '0')) }).catch(error => showToast(error.message || 'Fee setup could not be loaded', 'error'))
  }, [])

  useEffect(() => {
    try {
      const raw = sessionStorage.getItem('fee.deposit.prefill')
      if (raw) { setLegacyPrefill(JSON.parse(raw)); sessionStorage.removeItem('fee.deposit.prefill') }
    } catch { setLegacyPrefill(null) }
  }, [])

  const showToast = useCallback((msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }, [])

  const handleLoadStudent = useCallback(async (selectedId = studentId, overrides = {}) => {
    const selectedClassId = overrides.classId ?? classId
    const selectedAdmNo = overrides.admNo ?? admNo
    const selectedFeeBookNo = overrides.feeBookNo ?? feeBookNo
    if (!session) { showToast('Please select session.', 'error'); return }
    if (!selectedClassId && !selectedAdmNo && !selectedFeeBookNo) { showToast('Select class or enter admission no / fee book no.', 'error'); return }
    setLoading(true)
    try {
      const rows = await apiFetch('/fees/students/search?' + new URLSearchParams({ session, query: selectedAdmNo || '', classId: selectedClassId || '', feeBookNo: selectedFeeBookNo || '' }))
      setStudentOptions(rows)
      const found = selectedId ? rows.find(row => String(row.id || row.studentId) === String(selectedId)) : rows[0]
      if (!found) { showToast('Student not found.', 'error'); return }
      const mapped = { ...found, id: found.id || found.studentId, admNo: found.admNo || found.registrationNo, feeBookNo: found.feeBookNo || selectedFeeBookNo, name: found.name || found.studentName, class: found.classLabel || `${found.className || ''} ${found.sectionName || ''}`, father: found.father || found.fatherName || '', admDate: found.admDate || '' }
      setStudent(mapped)
      setStudentId(String(mapped.id))
      setAdmNo(mapped.admNo || '')
      setFeeBookNo(mapped.feeBookNo || '')
      // The old FeeBookNo/admission lookup selects the student's section
      // back into ddlClass after getstudetail returns.
      if (mapped.sectionId != null && String(mapped.sectionId) !== '') setClassId(String(mapped.sectionId))
      const [dues, summary, transactions] = await Promise.all([apiFetch(`/fees/students/${mapped.id}/deposit-flow?session=${encodeURIComponent(session)}&feeType=${encodeURIComponent(feeType || '0')}`), apiFetch(`/fees/students/${mapped.id}/summary`), apiFetch(`/fees/students/${mapped.id}/transactions`)])
      setFeeData(dues); setSummaryRows(summary); setTransactionRows(transactions); showToast(`Loaded: ${mapped.name}`)
    } catch (error) { showToast(error.message || 'Student fee data could not be loaded', 'error') } finally { setLoading(false) }
  }, [session, feeType, classId, admNo, feeBookNo, studentId, showToast])

  useEffect(() => {
    if (!legacyPrefill || !session || !feeType) return
    const admissionNo = legacyPrefill.admissionNo || ''
    if (!admissionNo) return
    const targetFeeType = String(legacyPrefill.feeTypeId ?? feeType)
    if (feeType !== targetFeeType) { setFeeType(targetFeeType); return }
    setAdmNo(admissionNo)
    handleLoadStudent('', { admNo: admissionNo })
    setLegacyPrefill(null)
  }, [legacyPrefill, session, feeType, handleLoadStudent])

  useEffect(() => {
    if (!session || !classId) { setStudentOptions([]); return undefined }
    let active = true
    apiFetch('/fees/students/search?' + new URLSearchParams({ session, classId }))
      .then(rows => { if (active) setStudentOptions(rows || []) })
      .catch(error => { if (active) showToast(error.message || 'Student list could not be loaded', 'error') })
    return () => { active = false }
  }, [session, classId, showToast])

  const handleClassChange = useCallback((event) => {
    setClassId(event.target.value)
    setAdmNo(''); setFeeBookNo(''); setStudentId(''); setStudent(null); setFeeData(null); setSummaryRows([]); setTransactionRows([])
  }, [])

  const handleStudentChange = useCallback((event) => {
    const id = event.target.value
    setStudentId(id)
    if (id) handleLoadStudent(id)
  }, [handleLoadStudent])

  const filteredStudents = useMemo(() => studentOptions, [studentOptions])

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-[#0f1117]">
      {/* ── TOP BAR (page header) ── */}
      <div className="sticky top-0 z-30 bg-white dark:bg-[#13172a] border-b border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-sm">
        <div className="flex items-center gap-3 px-4 py-3">
          <div className="flex items-center gap-2 flex-1 min-w-0">
            <div className="w-8 h-8 rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 flex items-center justify-center flex-shrink-0">
              <IndianRupee className="w-4 h-4 text-white" />
            </div>
            <div className="min-w-0">
              <h1 className="text-[15px] font-black text-slate-800 dark:text-slate-100 leading-tight">Fee Deposit</h1>
              <p className="text-[11px] text-slate-400 dark:text-slate-500 hidden sm:block">Collect and manage student fee payments</p>
            </div>
          </div>
          <div className="flex items-center gap-2 flex-shrink-0">
            {student && (
              <div className="hidden sm:flex items-center gap-2 px-3 py-1.5 rounded-xl bg-emerald-50 dark:bg-emerald-500/10 border border-emerald-200 dark:border-emerald-500/20">
                <div className="w-5 h-5 rounded-lg bg-emerald-600 flex items-center justify-center text-white text-[10px] font-bold">{student.name[0]}</div>
                <span className="text-[12px] font-semibold text-emerald-700 dark:text-emerald-300">{student.name}</span>
                <span className="text-[11px] text-emerald-600 dark:text-emerald-400">· {student.admNo}</span>
                <button onClick={() => { setStudent(null); setStudentId(''); setAdmNo('') }} className="ml-1 text-emerald-500 hover:text-emerald-700">
                  <X className="w-3.5 h-3.5" />
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto p-3 sm:p-4 space-y-4">
        {/* ── FILTER ROW ── */}
        <SectionCard>
          <div className="p-4">
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-7 gap-3 items-end">
              {/* Session */}
              <div>
                <p className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 mb-1">Session <span className="text-rose-500">*</span></p>
                <NativeSelect value={session} onChange={e => setSession(e.target.value)} placeholder="-- Session --">
                  {bootstrap.sessions.map(s => <option key={s.value} value={s.value}>{s.label || s.value}</option>)}
                </NativeSelect>
              </div>
              {/* Fee Type */}
              <div>
                <p className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 mb-1">Fee Type</p>
                <NativeSelect value={feeType} onChange={e => setFeeType(e.target.value)} placeholder="-- Fee Type --">
                  {bootstrap.feeTypes.map(f => <option key={f.id} value={f.id}>{f.name || f.label}</option>)}
                </NativeSelect>
              </div>
              {/* Adm No */}
              <div>
                <p className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 mb-1">Admission No.</p>
                <div className="relative">
                  <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                  <Input
                    value={admNo}
                    onChange={e => setAdmNo(e.target.value)}
                    onBlur={() => admNo.trim() && handleLoadStudent('', { classId: '', feeBookNo: '' })}
                    placeholder="Adm no. / name"
                    className="pl-8"
                  />
                </div>
              </div>
              {/* Class */}
              <div>
                <p className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 mb-1">Class</p>
                <NativeSelect value={classId} onChange={handleClassChange} placeholder="-- Class --">
                  {bootstrap.classes.map(c => <option key={c.id} value={c.id}>{c.label || c.className}</option>)}
                </NativeSelect>
              </div>
              {/* Student */}
              <div>
                <p className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 mb-1">Student</p>
                <NativeSelect value={studentId} onChange={handleStudentChange} placeholder="-- Student --">
                  {filteredStudents.map(s => <option key={s.id || s.studentId} value={s.id || s.studentId}>{s.name || s.studentName} ({s.admNo || s.registrationNo})</option>)}
                </NativeSelect>
              </div>
              {/* Fee Book No. — old page shows this for schools outside its excluded list */}
              {!['hrbl_123', 'Padma123', 'dpsbsr123'].includes(bootstrap.schoolId) && (
                <div>
                  <p className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 mb-1">Fee Book No.</p>
                  <Input
                    value={feeBookNo}
                    onChange={e => setFeeBookNo(e.target.value)}
                    onBlur={() => feeBookNo.trim() && handleLoadStudent('', { classId: '', admNo: '', feeBookNo: feeBookNo.trim() })}
                    placeholder="Fee Book No."
                    maxLength={8}
                  />
                </div>
              )}
              {/* Load button */}
              <div className="col-span-2 sm:col-span-1">
                <Btn
                  variant="primary"
                  size="md"
                  onClick={handleLoadStudent}
                  disabled={loading}
                  className="w-full"
                  icon={loading ? Loader2 : Eye}
                >
                  {loading ? 'Loading…' : 'Load Student'}
                </Btn>
              </div>
            </div>
          </div>
        </SectionCard>

        {/* ── STUDENT HEADER ── */}
        {student && <StudentHeader student={student} feeData={feeData} session={session} onPageClick={onPageClick} />}

        {/* ── EMPTY STATE ── */}
        {!student && !loading && (
          <div className="flex flex-col items-center justify-center py-16 gap-4 text-slate-400 dark:text-slate-600 rounded-2xl border-2 border-dashed border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35]">
            <div className="w-16 h-16 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
              <IndianRupee className="w-8 h-8 opacity-40" />
            </div>
            <div className="text-center">
              <p className="text-[15px] font-bold text-slate-500 dark:text-slate-400">No student selected</p>
              <p className="text-[13px] text-slate-400 dark:text-slate-500 mt-1">
                Select session &amp; class, then click <strong>Load Student</strong>
              </p>
            </div>
          </div>
        )}

        {/* ── TABS (only when student loaded) ── */}
        {student && (
          <div className="space-y-3">
            {/* Tab Nav — Desktop */}
            <div className="hidden sm:flex items-stretch rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
              {TABS.map(tab => {
                const Icon = tab.icon
                return (
                  <button
                    key={tab.id}
                    type="button"
                    onClick={() => setActiveTab(tab.id)}
                    className={`flex-1 flex items-center justify-center gap-2 py-3 px-2 text-[12px] font-semibold transition-all relative
                      ${activeTab === tab.id
                        ? 'bg-blue-600 text-white dark:bg-indigo-600 shadow-inner'
                        : 'text-slate-500 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-white/[0.03]'}`}
                  >
                    <Icon className="w-3.5 h-3.5 flex-shrink-0" />
                    <span className="hidden lg:block">{tab.label}</span>
                    <span className="lg:hidden">{tab.shortLabel}</span>
                    {activeTab === tab.id && (
                      <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-300 dark:bg-indigo-300 rounded-full" />
                    )}
                  </button>
                )
              })}
            </div>

            {/* Tab Nav — Mobile scroll */}
            <div className="sm:hidden flex overflow-x-auto gap-0 rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm no-scrollbar">
              {TABS.map(tab => {
                const Icon = tab.icon
                return (
                  <button
                    key={tab.id}
                    type="button"
                    onClick={() => setActiveTab(tab.id)}
                    className={`flex-shrink-0 flex flex-col items-center justify-center gap-1 py-2.5 px-4 text-[10px] font-semibold transition-all min-w-[72px]
                      ${activeTab === tab.id
                        ? 'bg-blue-600 text-white dark:bg-indigo-600'
                        : 'text-slate-400 dark:text-slate-500'}`}
                  >
                    <Icon className="w-4 h-4" />
                    {tab.shortLabel}
                  </button>
                )
              })}
            </div>

            {/* Tab Content */}
            <div>
              {activeTab === 0 && <DepositFeeTab     student={student} feeData={feeData} showToast={showToast} session={session} feeType={feeType} bootstrap={bootstrap} />}
              {activeTab === 1 && <FeeSummaryTab     rows={summaryRows} />}
              {activeTab === 2 && <PreviousTxnTab    rows={transactionRows} showToast={showToast} studentId={student.id} session={session} />}
          {activeTab === 3 && <DepositMiscFeeTab showToast={showToast} student={student} session={session} bootstrap={bootstrap} />}
              {activeTab === 4 && <AssignOptionalFeeTab showToast={showToast} session={session} bootstrap={bootstrap} student={student} />}
              {activeTab === 5 && <AdjustmentDetailsTab student={student} session={session} showToast={showToast} />}
            </div>
          </div>
        )}
      </div>

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}

      {/* Global style for hiding scrollbar */}
      <style>{`
        .no-scrollbar::-webkit-scrollbar { display: none; }
        .no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
        @media (min-width: 640px) {
          .hidden-on-mobile-step2 { display: block !important; }
        }
      `}</style>
    </div>
  )
}
